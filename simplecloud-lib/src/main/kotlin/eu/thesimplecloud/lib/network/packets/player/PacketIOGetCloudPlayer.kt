package eu.thesimplecloud.lib.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.player.ICloudPlayer
import java.util.*

class PacketIOGetCloudPlayer() : JsonPacket() {


    constructor(name: String): this() {
        this.jsonData.append("name", name)
    }

    constructor(uniqueId: UUID): this() {
        this.jsonData.append("uniqueId", uniqueId)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<ICloudPlayer> {
        val name = this.jsonData.getString("name")
        val uniqueId = this.jsonData.getObject("uniqueId", UUID::class.java)
        var cloudPlayer: ICloudPlayer? = null
        if (name != null) {
            cloudPlayer = CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(name)
        }
        if (uniqueId != null) {
            cloudPlayer = CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(uniqueId)
        }
        return CommunicationPromise.ofNullable(cloudPlayer, NoSuchElementException("Player not found"))
    }
}