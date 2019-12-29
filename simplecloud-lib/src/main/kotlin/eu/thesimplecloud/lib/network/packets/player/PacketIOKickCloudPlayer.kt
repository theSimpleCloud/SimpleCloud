package eu.thesimplecloud.lib.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.player.ICloudPlayer
import java.util.*

class PacketIOKickCloudPlayer() : JsonPacket() {

    constructor(cloudPlayer: ICloudPlayer, message: String) : this() {
        this.jsonData.append("playerUniqueId", cloudPlayer.getUniqueId()).append("message", message)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return contentException("playerUniqueId")
        val message = this.jsonData.getString("message") ?: return contentException("message")
        CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)?.kick(message)
        return unit()
    }
}