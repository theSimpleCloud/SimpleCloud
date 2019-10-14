package eu.thessimplecloud.client.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue
import eu.thesimplecloud.lib.client.CloudClientType

class PacketOutCloudClientLogin() : JsonPacket() {

    constructor(name: String, cloudClientType: CloudClientType) : this() {
        this.jsonData.append("name", name).append("cloudClientType", cloudClientType)
    }

    constructor(cloudClientType: CloudClientType) : this() {
        this.jsonData.append("cloudClientType", cloudClientType)
    }

    override suspend fun handle(connection: IConnection): IPacket? = null


}