package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.network.packets.service.PacketIOStartCloudService
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIOCreateServiceGroup
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIODeleteServiceGroup
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.impl.DefaultCloudService
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.servicegroup.impl.AbstractCloudServiceGroupManager

class CloudServiceGroupManagerImpl : AbstractCloudServiceGroupManager() {


    override fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit> {
        return Wrapper.instance.communicationClient.sendUnitQuery(PacketIOCreateServiceGroup(cloudServiceGroup))
    }

    override fun startNewService(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudService> {
        return Wrapper.instance.communicationClient.sendQuery<DefaultCloudService>(PacketIOStartCloudService(cloudServiceGroup.getName())) as ICommunicationPromise<ICloudService>
    }

    override fun deleteServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit> {
        return Wrapper.instance.communicationClient.sendUnitQuery(PacketIODeleteServiceGroup(cloudServiceGroup.getName()))
    }


}