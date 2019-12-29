package eu.thesimplecloud.lib.network.packets.service

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib

class PacketIORemoveCloudService() : JsonPacket() {

    constructor(serviceName: String) : this() {
        this.jsonData.append("serviceName", serviceName)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val serviceName = this.jsonData.getString("serviceName") ?: return contentException("serviceName")
        CloudLib.instance.getCloudServiceManger().removeCloudService(serviceName)
        return unit()
    }
}