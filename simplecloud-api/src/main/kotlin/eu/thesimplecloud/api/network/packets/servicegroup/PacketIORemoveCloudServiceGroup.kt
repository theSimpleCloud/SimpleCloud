package eu.thesimplecloud.api.network.packets.servicegroup

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIORemoveCloudServiceGroup() : JsonPacket() {

    constructor(cloudServiceGroupName: String) : this() {
        this.jsonData.append("groupName", cloudServiceGroupName)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val groupName = this.jsonData.getString("groupName") ?: return contentException("groupName")
        val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(groupName) ?: return failure(NoSuchElementException("Service is not registered"))
        CloudAPI.instance.getCloudServiceGroupManager().deleteServiceGroup(serviceGroup)
        return unit()
    }
}