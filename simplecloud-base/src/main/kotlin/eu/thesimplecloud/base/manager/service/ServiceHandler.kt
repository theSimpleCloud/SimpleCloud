package eu.thesimplecloud.base.manager.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.network.packets.service.PacketIOUpdateCloudService
import eu.thesimplecloud.api.network.packets.service.PacketIOWrapperStartService
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.service.impl.DefaultCloudService
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

    private fun startMinServices() {
        for (serviceGroup in CloudAPI.instance.getCloudServiceGroupManager().getAllGroups()) {
            val allServices = serviceGroup.getAllServices()
            //don't exclude closed services because they will be deleted in a moment.
            val inLobbyServices = allServices.filter { it.getState() != ServiceState.INVISIBLE }
            val services = inLobbyServices.filter { it.getOnlinePercentage() < serviceGroup.getPercentToStartNewService().toDouble() / 100 }
            var newServicesAmount = serviceGroup.getMinimumOnlineServiceCount() - services.size
            if (serviceGroup.getMaximumOnlineServiceCount() != -1 && newServicesAmount + services.size > serviceGroup.getMaximumOnlineServiceCount())
                newServicesAmount = serviceGroup.getMaximumOnlineServiceCount() - services.size
            if (newServicesAmount > 0) {
                startServicesByGroup(serviceGroup, newServicesAmount)
            }
        }
    }

    private fun stopRedundantServices() {
        for (serviceGroup in CloudAPI.instance.getCloudServiceGroupManager().getAllGroups()) {
            val allServices = serviceGroup.getAllServices()
            val inLobbyServices = allServices.filter { it.getState() == ServiceState.VISIBLE }
            val stoppableServices = inLobbyServices
                    .filter { (it.getLastUpdate() + TimeUnit.MINUTES.toMillis(3)) < System.currentTimeMillis() }
                    .filter { it.getOnlinePlayers() <= 0 }
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
                    for (priority in 0..maxPriority) {
                        val services = priorityToServices[priority] ?: emptyList()
                        //false will be listed first -> services with wrapper will be listed first
                        val sortedServices = services.sortedBy { it.getWrapperName().isEmpty() }
                        for (service in sortedServices) {
                            val wrapper = getWrapperForService(service) ?: continue
                            val wrapperClient = Manager.instance.communicationServer.getClientManager().getClientByClientValue(wrapper)
                            wrapperClient ?: continue
                            service as DefaultCloudService
                            service.setWrapperName(wrapper.getName())
                            service.update()
                            wrapperClient.sendUnitQuery(PacketIOUpdateCloudService(service)).awaitUninterruptibly()
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