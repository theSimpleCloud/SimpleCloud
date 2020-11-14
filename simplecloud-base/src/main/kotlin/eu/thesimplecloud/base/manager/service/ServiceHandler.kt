/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.base.manager.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.network.packets.service.PacketIOWrapperStartService
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.service.impl.DefaultCloudService
import eu.thesimplecloud.api.service.startconfiguration.IServiceStartConfiguration
import eu.thesimplecloud.api.service.startconfiguration.ServiceStartConfiguration
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.utils.time.Timestamp
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.startup.Launcher
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.math.max
import kotlin.math.min

class ServiceHandler : IServiceHandler {

    private val serviceMinimumCountCalculator = ServiceMinimumCountCalculator()
    private var serviceQueue: MutableList<ICloudService> = ArrayList()

    override fun startServicesByGroup(cloudServiceGroup: ICloudServiceGroup, count: Int): List<ICloudService> {
        require(count >= 1) { "Count must be positive" }
        val list = ArrayList<ICloudService>()
        for (i in 0 until count) {
            list.add(startService(ServiceStartConfiguration(cloudServiceGroup)))
        }
        return list
    }

    override fun startService(startConfiguration: IServiceStartConfiguration): ICloudService {
        startConfiguration as ServiceStartConfiguration
        val cloudServiceGroup = startConfiguration.getServiceGroup()
        val serviceNumber = startConfiguration.serviceNumber ?: getNumberForNewService(cloudServiceGroup)
        val serviceName = cloudServiceGroup.getName() + "-" + serviceNumber
        val runningService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(serviceName)
        if (runningService != null) throw IllegalArgumentException("Service to start ($serviceName) is already registered")
        val service = DefaultCloudService(
                cloudServiceGroup.getName(),
                serviceNumber,
                UUID.randomUUID(),
                startConfiguration.template,
                cloudServiceGroup.getWrapperName(),
                -1,
                startConfiguration.maxMemory,
                startConfiguration.maxPlayers,
                "Cloud service"
        )
        CloudAPI.instance.getCloudServiceManager().update(service).awaitUninterruptibly()
        addServiceToQueue(service)
        return service
    }

    override fun removeServiceFromQueue(cloudService: ICloudService) {
        this.serviceQueue.remove(cloudService)
    }

    private fun addServiceToQueue(service: ICloudService) {
        if (serviceQueue.contains(service))
            return
        serviceQueue.add(service)
        Launcher.instance.consoleSender.sendProperty("manager.service.queued", service.getName())
    }


    private fun getNumberForNewService(cloudServiceGroup: ICloudServiceGroup): Int {
        var number = 1
        while (CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(cloudServiceGroup.getName() + "-" + number) != null)
            number++
        return number
    }

    private fun startMinServices() {
        for (serviceGroup in CloudAPI.instance.getCloudServiceGroupManager().getAllCachedObjects()) {
            val allServices = serviceGroup.getAllServices()
            //don't exclude closed services because they will be deleted in a moment.
            val inLobbyServices = allServices.filter { it.getState() != ServiceState.INVISIBLE } //1
            val inLobbyServicesWithFewPlayers = inLobbyServices.filter { it.getOnlinePercentage() < serviceGroup.getPercentToStartNewService().toDouble() / 100 }
            val minimumServiceCount = getMinimumServiceCount(serviceGroup)
            var newServicesAmount = minimumServiceCount - inLobbyServicesWithFewPlayers.size
            if (serviceGroup.getMaximumOnlineServiceCount() != -1 && newServicesAmount + inLobbyServices.size > serviceGroup.getMaximumOnlineServiceCount())
                newServicesAmount = serviceGroup.getMaximumOnlineServiceCount() - inLobbyServices.size
            if (newServicesAmount > 0) {
                startServicesByGroup(serviceGroup, newServicesAmount)
            }
        }
    }

    private fun stopRedundantServices() {
        for (serviceGroup in CloudAPI.instance.getCloudServiceGroupManager().getAllCachedObjects()) {
            val allServices = serviceGroup.getAllServices()
            val redundantServices = allServices.filter { it.getState() == ServiceState.VISIBLE }
                    .filter { (it.getOnlinePercentage() * 100) < serviceGroup.getPercentToStartNewService() }
                    .filter { it.getLastPlayerUpdate().hasTimePassed(TimeUnit.MINUTES.toMillis(3)) }
            //exclude services with percentage higher than percentage to start new service because they are not redundant
            val stoppableServices = redundantServices.filter { it.getOnlineCount() <= 0 }
            val minimumServiceCount = getMinimumServiceCount(serviceGroup)
            if (redundantServices.size > minimumServiceCount) {
                val amountToStop = redundantServices.size - minimumServiceCount
                for (i in 0 until min(amountToStop, stoppableServices.size)) {
                    val service = stoppableServices[i]
                    //set to invisible, so that services are not shutdown again
                    service.setState(ServiceState.INVISIBLE)
                    service.update().awaitUninterruptibly()
                    service.shutdown()
                }
            }
        }
    }

    fun startThread() {
        thread(start = true, isDaemon = true) {
            while (true) {
                this.serviceQueue = this.serviceQueue.sortedByDescending { it.getServiceGroup().getStartPriority() }.toMutableList()
                startMinServices()
                stopRedundantServices()

                val priorityToServices = this.serviceQueue.groupBy { it.getServiceGroup().getStartPriority() }
                val maxPriority = priorityToServices.keys.maxOrNull()

                if (maxPriority != null) {
                    for (priority in maxPriority downTo 0) {
                        val services = priorityToServices[priority] ?: emptyList()
                        //false will be listed first -> services with wrapper will be listed first
                        val sortedServices = services.sortedBy { it.getWrapperName() == null }
                        for (service in sortedServices) {
                            executeStart(service)
                        }
                    }
                }
                Thread.sleep(300)
            }
        }
    }

    private fun executeStart(service: ICloudService) {
        val wrapper = getWrapperForService(service) ?: return
        val wrapperClient = Manager.instance.communicationServer.getClientManager().getClientByClientValue(wrapper)
        wrapperClient ?: return
        service as DefaultCloudService
        service.setWrapperName(wrapper.getName())
        service.setLastPlayerUpdate(Timestamp())
        service.update().awaitUninterruptibly()
        wrapperClient.sendUnitQuery(PacketIOWrapperStartService(service.getName())).awaitUninterruptibly()
        Launcher.instance.consoleSender.sendProperty("manager.service.start", wrapper.getName(), service.getName())
        this.serviceQueue.remove(service)
    }

    private fun getWrapperForService(service: ICloudService): IWrapperInfo? {
        if (service.getWrapperName() == null) {
            return CloudAPI.instance.getWrapperManager().getWrapperByUnusedMemory(service.getMaxMemory())
        } else {
            val requiredWrapper = CloudAPI.instance.getWrapperManager().getWrapperByName(service.getWrapperName()!!)
                    ?: return null
            if (requiredWrapper.hasEnoughMemory(service.getMaxMemory()) && requiredWrapper.isAuthenticated()
                    && requiredWrapper.hasTemplatesReceived()
                    && requiredWrapper.getCurrentlyStartingServices() != requiredWrapper.getMaxSimultaneouslyStartingServices()) {
                return requiredWrapper
            }
            return null
        }
    }

    fun getMinimumServiceCount(group: ICloudServiceGroup): Int {
        return max(group.getMinimumOnlineServiceCount(), this.serviceMinimumCountCalculator.getCalculatedMinimumServiceCount(group))
    }


}