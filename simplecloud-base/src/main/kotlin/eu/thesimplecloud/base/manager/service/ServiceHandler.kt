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
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.math.min

class ServiceHandler : IServiceHandler {

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
        CloudAPI.instance.getCloudServiceManager().update(service)
        addServiceToQueue(service)
        return service
    }

    private fun addServiceToQueue(service: ICloudService) {
        if (serviceQueue.contains(service))
            return
        serviceQueue.add(service)
        Launcher.instance.consoleSender.sendMessage("manager.service.queued", "Service %SERVICE%", service.getName(), " is now queued")
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
            var newServicesAmount = serviceGroup.getMinimumOnlineServiceCount() - inLobbyServicesWithFewPlayers.size
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
            val inLobbyServices = allServices.filter { it.getState() == ServiceState.VISIBLE }
            val stoppableServices = inLobbyServices
                    .filter { (it.getLastUpdate() + TimeUnit.MINUTES.toMillis(3)) < System.currentTimeMillis() }
                    .filter { it.getOnlineCount() <= 0 }
            if (inLobbyServices.size > serviceGroup.getMinimumOnlineServiceCount()) {
                val amountToStop = inLobbyServices.size - serviceGroup.getMinimumOnlineServiceCount()
                for (i in 0 until min(amountToStop, stoppableServices.size)) {
                    val service = stoppableServices[i]
                    //set to invisible, so that services are not shutdown again
                    service.setState(ServiceState.INVISIBLE)
                    service.update()
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
                val maxPriority = priorityToServices.keys.max()
                if (maxPriority != null) {
                    for (priority in maxPriority downTo 0) {
                        val services = priorityToServices[priority] ?: emptyList()
                        //false will be listed first -> services with wrapper will be listed first
                        val sortedServices = services.sortedBy { it.getWrapperName() == null }
                        for (service in sortedServices) {
                            val wrapper = getWrapperForService(service) ?: continue
                            val wrapperClient = Manager.instance.communicationServer.getClientManager().getClientByClientValue(wrapper)
                            wrapperClient ?: continue
                            service as DefaultCloudService
                            service.setWrapperName(wrapper.getName())
                            service.update()
                            CloudAPI.instance.getCloudServiceManager().sendUpdateToConnection(service, wrapperClient).awaitUninterruptibly()
                            wrapperClient.sendUnitQuery(PacketIOWrapperStartService(service.getName())).awaitUninterruptibly()
                            Launcher.instance.consoleSender.sendMessage("manager.service.start", "Told Wrapper %WRAPPER%", wrapper.getName(), " to start service %SERVICE%", service.getName())
                            this.serviceQueue.remove(service)
                        }
                    }
                }
                Thread.sleep(300)
            }
        }
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


}