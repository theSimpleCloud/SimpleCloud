package eu.thesimplecloud.lib.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.lib.CloudLib
import java.util.*

class PacketIOGetCloudPlayer() : JsonPacket() {


    constructor(name: String): this() {
        this.jsonData.append("name", name)
    }

    constructor(uniqueId: UUID): this() {
        this.jsonData.append("uniqueId", uniqueId)
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val name = this.jsonData.getString("name")
        val uniqueId = this.jsonData.getObject("uniqueId", UUID::class.java)
        if (name != null) {
            return CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(name)?.let { ObjectPacket.getNewObjectPacketWithContent(it) }
        }
        if (uniqueId != null) {
            return CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(uniqueId)?.let { ObjectPacket.getNewObjectPacketWithContent(it) }
        }
        return null
    }
}