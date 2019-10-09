package eu.thesimplecloud.lib.packets.service

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.lib.CloudLib

class PacketIORemoveCloudService() : JsonPacket() {

    constructor(serviceName: String) : this() {
        this.jsonData.append("serviceName", serviceName)
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val serviceName = this.jsonData.getString("serviceName") ?: return null
        CloudLib.instance.getCloudServiceManger().removeCloudService(serviceName)
        return null
    }
}