package eu.thesimplecloud.base.manager.service

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.network.packets.service.PacketIOWrapperStartService
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceState
import eu.thesimplecloud.lib.service.impl.DefaultCloudService
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.ArrayList

class ServiceHandler : IServiceHandler {

    private val serviceQueue = LinkedBlockingQueue<ICloudService>()

    override fun startServicesByGroup(cloudServiceGroup: ICloudServiceGroup, count: Int): List<ICloudService> {
        require(count >= 1) { "Count must be positive." }
        val list = ArrayList<ICloudService>()
        for (i in 0 until count) {
            val service = DefaultCloudService(cloudServiceGroup.getName(), getNumberForNewService(cloudServiceGroup), UUID.randomUUID(), cloudServiceGroup.getTemplateName(), "", -1, cloudServiceGroup.getMaxMemory(), "Cloud service")
            CloudLib.instance.getCloudServiceManger().updateCloudService(service)
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
        while (CloudLib.instance.getCloudServiceManger().getCloudService(cloudServiceGroup.getName() + "-" + number) != null)
            number++
        return number
    }

    fun startMinServices(){
        for (serviceGroup in CloudLib.instance.getCloudServiceGroupManager().getAllGroups()) {
            val allServices = serviceGroup.getAllServices()
            val inLobbyServices = allServices.filter { it.getState() != ServiceState.INGAME && it.getState() != ServiceState.CLOSED }
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
        GlobalScope.launch {
            while (true) {
                startMinServices()
                if (serviceQueue.isNotEmpty()) {
                    val service = serviceQueue.poll()
                    val wrapperInfo = if (service.getWrapperName().isBlank()){
                        CloudLib.instance.getWrapperManager().getWrapperByUnusedMemory(service.getMaxMemory())
                    } else {
                        //TODO check free ram
                        CloudLib.instance.getWrapperManager().getWrapperByName(service.getWrapperName());
                    }
                    val wrapperClient = wrapperInfo?.let { Manager.instance.communicationServer.getClientManager().getClientByClientValue(it) }
                    if (wrapperClient != null){
                        service as DefaultCloudService
                        service.setWrapperName(wrapperInfo.getName())
                        CloudLib.instance.getCloudServiceManger().updateCloudService(service)
                        wrapperClient.sendQuery(PacketIOWrapperStartService(service.getName()))
                        Launcher.instance.consoleSender.sendMessage("manager.service.start", "Told Wrapper %WRAPPER%", wrapperInfo.getName(), " to start service %SERVICE%", service.getName())
                    } else {
                        serviceQueue.add(service)
                    }
                }

                Thread.sleep(200)
            }
        }
    }


}