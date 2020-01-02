package eu.thesimplecloud.plugin.network.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection

class PacketOutCreateCloudPlayer() : JsonPacket() {

    constructor(defaultPlayerConnection: DefaultPlayerConnection, proxyName: String): this() {
        this.jsonData.append("playerConnection", defaultPlayerConnection).append("proxyName", proxyName)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> = unit()
}