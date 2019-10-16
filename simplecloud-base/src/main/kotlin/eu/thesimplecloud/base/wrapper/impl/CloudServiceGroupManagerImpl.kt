package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIOCreateServiceGroup
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIODeleteServiceGroup
import eu.thesimplecloud.lib.network.reponsehandler.CloudServiceGroupResponseHandler
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.servicegroup.impl.AbstractCloudServiceGroupManager

class CloudServiceGroupManagerImpl : AbstractCloudServiceGroupManager() {

    override fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudServiceGroup> {
        return Wrapper.instance.communicationClient.sendQuery(PacketIOCreateServiceGroup(cloudServiceGroup), CloudServiceGroupResponseHandler())
    }

    override fun startNewService(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudService> = TODO()

    override fun deleteServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit> {
        return Wrapper.instance.communicationClient.sendQuery(PacketIODeleteServiceGroup(cloudServiceGroup.getName()))
    }


}