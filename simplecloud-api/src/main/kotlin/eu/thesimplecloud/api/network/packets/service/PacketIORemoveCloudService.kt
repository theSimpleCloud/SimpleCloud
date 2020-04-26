package eu.thesimplecloud.api.network.packets.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIORemoveCloudService() : JsonPacket() {

    constructor(serviceName: String) : this() {
        this.jsonData.append("serviceName", serviceName)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val serviceName = this.jsonData.getString("serviceName") ?: return contentException("serviceName")
        CloudAPI.instance.getCloudServiceManager().removeCloudService(serviceName)
        return unit()
    }
}