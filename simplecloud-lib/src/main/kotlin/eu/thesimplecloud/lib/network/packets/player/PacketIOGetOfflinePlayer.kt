package eu.thesimplecloud.lib.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import java.util.*

class PacketIOGetOfflinePlayer() : JsonPacket() {

    constructor(uniqueId: UUID): this() {
        this.jsonData.append("uniqueId", uniqueId)
    }

    constructor(name: String): this() {
        this.jsonData.append("name", name)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val uniqueId = this.jsonData.getObject("uniqueId", UUID::class.java)
        val name = this.jsonData.getString("name")
        if (uniqueId == null && name == null) return contentException("name and uniqueId null")
        if (uniqueId != null) return CloudLib.instance.getCloudPlayerManager().getOfflineCloudPlayer(uniqueId)
        return CloudLib.instance.getCloudPlayerManager().getOfflineCloudPlayer(name!!)
    }
}