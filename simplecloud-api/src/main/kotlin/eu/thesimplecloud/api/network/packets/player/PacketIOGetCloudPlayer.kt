package eu.thesimplecloud.api.network.packets.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
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
            cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(name)
        }
        if (uniqueId != null) {
            cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(uniqueId)
        }
        return CommunicationPromise.ofNullable(cloudPlayer, NoSuchElementException("Player not found"))
    }
}