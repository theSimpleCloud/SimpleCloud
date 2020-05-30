package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.api.event.service.CloudServiceConnectedEvent
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.listenerextension.cloudListener
import eu.thesimplecloud.api.network.packets.service.PacketIOStopCloudService
import eu.thesimplecloud.api.network.packets.service.PacketIOWrapperStartService
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.service.impl.AbstractCloudServiceManager
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class CloudServiceManagerImpl : AbstractCloudServiceManager() {

    override fun stopService(cloudService: ICloudService): ICommunicationPromise<Unit> {
        if (cloudService.getState() == ServiceState.PREPARED) {
            return CommunicationPromise.failed(IllegalStateException("Service is not running"))
        }
        val wrapper = cloudService.getWrapper()
        val wrapperClient = Manager.instance.communicationServer.getClientManager()
                .getClientByClientValue(wrapper)
        wrapperClient?.sendUnitQuery(PacketIOStopCloudService(cloudService.getName()))
        return cloudListener<CloudServiceUnregisteredEvent>()
                .addCondition { it.cloudService == cloudService }
                .unregisterAfterCall()
                .toUnitPromise()
    }

    override fun startService(cloudService: ICloudService): ICommunicationPromise<Unit> {
        if (cloudService.isActive() || cloudService.getState() == ServiceState.CLOSED) throw IllegalStateException("Can not start started service.")
        val wrapper = cloudService.getWrapper()
        val wrapperClient = Manager.instance.communicationServer.getClientManager().getClientByClientValue(wrapper)
        check(wrapperClient != null) { "Can not find client of wrapper to start service ${cloudService.getName()} on." }
        wrapperClient.sendUnitQuery(PacketIOWrapperStartService(cloudService.getName()))
        return cloudListener<CloudServiceConnectedEvent>()
                .addCondition { it.cloudService == cloudService }
                .unregisterAfterCall()
                .toUnitPromise()
    }
}