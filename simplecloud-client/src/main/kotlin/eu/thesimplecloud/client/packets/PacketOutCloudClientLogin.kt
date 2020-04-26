package eu.thesimplecloud.client.packets

import eu.thesimplecloud.api.client.CloudClientType
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket

class PacketOutCloudClientLogin() : JsonPacket() {

    constructor(cloudClientType: CloudClientType, name: String) : this() {
        this.jsonData.append("name", name).append("cloudClientType", cloudClientType)
    }

    constructor(cloudClientType: CloudClientType) : this() {
        this.jsonData.append("cloudClientType", cloudClientType)
    }

    override suspend fun handle(connection: IConnection) = unit()


}