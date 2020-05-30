package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.listenerextension.cloudListener
import eu.thesimplecloud.api.network.packets.service.PacketIOStopCloudService
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.impl.AbstractCloudServiceManager
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.plugin.startup.CloudPlugin

class CloudServiceManagerImpl : AbstractCloudServiceManager() {

    override fun stopService(cloudService: ICloudService): ICommunicationPromise<Unit> {
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOStopCloudService(cloudService.getName()))
        return cloudListener<CloudServiceUnregisteredEvent>()
                .addCondition { it.cloudService == cloudService }
                .unregisterAfterCall()
                .toUnitPromise()
    }
}