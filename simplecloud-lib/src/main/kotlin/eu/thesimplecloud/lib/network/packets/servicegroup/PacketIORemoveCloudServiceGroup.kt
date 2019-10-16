package eu.thesimplecloud.lib.network.packets.servicegroup

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.lib.CloudLib

class PacketIORemoveCloudServiceGroup() : JsonPacket() {

    constructor(cloudServiceGroupName: String) : this() {
        this.jsonData.append("groupName", cloudServiceGroupName)
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val groupName = this.jsonData.getString("groupName") ?: return null
        val serviceGroup = CloudLib.instance.getCloudServiceGroupManager().getServiceGroup(groupName) ?: return null
        CloudLib.instance.getCloudServiceGroupManager().deleteServiceGroup(serviceGroup)
        return null
    }
}