package eu.thesimplecloud.base.manager.service

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.network.packets.service.PacketIOUpdateCloudService
import eu.thesimplecloud.api.network.packets.service.PacketIOWrapperStartService
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.service.impl.DefaultCloudService
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class ServiceHandler : IServiceHandler {

    private val serviceQueue = LinkedBlockingQueue<ICloudService>()

    override fun startServicesByGroup(cloudServiceGroup: ICloudServiceGroup, count: Int): List<ICloudService> {
        require(count >= 1) { "Count must be positive." }
        val list = ArrayList<ICloudService>()
        for (i in 0 until count) {
            val service = DefaultCloudService(cloudServiceGroup.getName(), getNumberForNewService(cloudServiceGroup), UUID.randomUUID(), cloudServiceGroup.getTemplateName(), cloudServiceGroup.getWrapperName()
                    ?: "", -1, cloudServiceGroup.getMaxMemory(), "Cloud service")
            CloudAPI.instance.getCloudServiceManager().updateCloudService(service)
            list.add(service)
            addServiceToQueue(service)
        }
        return list
    }

    private fun addServiceToQueue(service: ICloudService) {
        if (serviceQueue.contains(service))
            return
        serviceQueue.add(service)
        Launcher.instance.consoleSender.sendMessage("manager.service.queued", "Service %SERVICE%", service.getName(), " is now queued.")
    }


    private fun getNumberForNewService(cloudServiceGroup: ICloudServiceGroup): Int {
        var number = 1
        while (CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(cloudServiceGroup.getName() + "-" + number) != null)
            number++
        return number
    }

    fun startMinServices() {
        for (serviceGroup in CloudAPI.instance.getCloudServiceGroupManager().getAllGroups()) {
            val allServices = serviceGroup.getAllServices()
            val inLobbyServices = allServices.filter { it.getState() != ServiceState.INVISIBLE && it.getState() != ServiceState.CLOSED }
            val services = inLobbyServices.filter { it.getOnlinePercentage() < serviceGroup.getPercentToStartNewService().toDouble() / 100 }
            var newServicesAmount = serviceGroup.getMinimumOnlineServiceCount() - services.size
            if (serviceGroup.getMaximumOnlineServiceCount() != -1 && newServicesAmount + services.size > serviceGroup.getMaximumOnlineServiceCount())
                newServicesAmount = serviceGroup.getMaximumOnlineServiceCount() - services.size
            if (newServicesAmount > 0) {
                startServicesByGroup(serviceGroup, newServicesAmount)
            }
        }
    }

    fun startThread() {
        thread(start = true, isDaemon = true) {
            while (true) {
                startMinServices()
                if (serviceQueue.isNotEmpty()) {
                    val service = serviceQueue.poll()
                    val wrapperInfo = getWrapperForService(service)
                    val wrapperClient = wrapperInfo?.let { Manager.instance.communicationServer.getClientManager().getClientByClientValue(it) }
                    if (wrapperClient != null) {
                        service as DefaultCloudService
                        service.setWrapperName(wrapperInfo.getName())
                        CloudAPI.instance.getCloudServiceManager().updateCloudService(service)
                        wrapperClient.sendUnitQuery(PacketIOUpdateCloudService(service)).syncUninterruptibly()
                        wrapperClient.sendUnitQuery(PacketIOWrapperStartService(service.getName())).syncUninterruptibly()
                        Launcher.instance.consoleSender.sendMessage("manager.service.start", "Told Wrapper %WRAPPER%", wrapperInfo.getName(), " to start service %SERVICE%", service.getName())
                    } else {
                        serviceQueue.add(service)
                    }
                }
                Thread.sleep(100)
            }
        }
    }

    private fun getWrapperForService(service: ICloudService): IWrapperInfo? {
        if (service.getWrapperName().isBlank()) {
            return CloudAPI.instance.getWrapperManager().getWrapperByUnusedMemory(service.getMaxMemory())
        } else {
            val requiredWrapper = CloudAPI.instance.getWrapperManager().getWrapperByName(service.getWrapperName())
                    ?: return null
            if (requiredWrapper.hasEnoughMemory(service.getMaxMemory()) && requiredWrapper.isAuthenticated() && requiredWrapper.hasTemplatesReceived()) {
                return requiredWrapper
            }
            return null
        }
    }


}