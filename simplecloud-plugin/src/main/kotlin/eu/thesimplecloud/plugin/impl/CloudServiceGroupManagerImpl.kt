package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.network.packets.service.PacketIOStartCloudService
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIOCreateServiceGroup
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIODeleteServiceGroup
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.servicegroup.impl.AbstractCloudServiceGroupManager
import eu.thesimplecloud.plugin.startup.CloudPlugin
import java.lang.IllegalStateException

class CloudServiceGroupManagerImpl : AbstractCloudServiceGroupManager() {

    override fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit> {
        return CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOCreateServiceGroup(cloudServiceGroup))
    }

    override fun startNewService(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudService> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOStartCloudService(cloudServiceGroup.getName()))
    }

    override fun deleteServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIODeleteServiceGroup(cloudServiceGroup.getName()))
    }

}