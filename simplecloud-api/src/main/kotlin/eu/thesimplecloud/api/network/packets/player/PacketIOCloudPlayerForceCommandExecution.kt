package eu.thesimplecloud.api.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer
import java.util.*

class PacketIOCloudPlayerForceCommandExecution() : JsonPacket() {

    constructor(cloudPlayer: ICloudPlayer, command: String) : this() {
        this.jsonData.append("playerUniqueId", cloudPlayer.getUniqueId())
                .append("command", command)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return contentException("playerUniqueId")
        val command = this.jsonData.getString("command") ?: return contentException("command")
        CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)?.forceCommandExecution(command)
        return unit()
    }
}