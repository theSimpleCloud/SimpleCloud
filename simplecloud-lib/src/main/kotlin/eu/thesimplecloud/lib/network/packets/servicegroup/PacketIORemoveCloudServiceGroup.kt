package eu.thesimplecloud.lib.network.packets.servicegroup

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib

class PacketIORemoveCloudServiceGroup() : JsonPacket() {

    constructor(cloudServiceGroupName: String) : this() {
        this.jsonData.append("groupName", cloudServiceGroupName)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val groupName = this.jsonData.getString("groupName") ?: return contentException("groupName")
        val serviceGroup = CloudLib.instance.getCloudServiceGroupManager().getServiceGroup(groupName) ?: return failure(NoSuchElementException("Service is not registered"))
        CloudLib.instance.getCloudServiceGroupManager().deleteServiceGroup(serviceGroup)
        return unit()
    }
}