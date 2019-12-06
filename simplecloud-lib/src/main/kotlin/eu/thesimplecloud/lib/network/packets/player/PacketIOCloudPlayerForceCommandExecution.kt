package eu.thesimplecloud.lib.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.player.ICloudPlayer
import java.util.*

class PacketIOCloudPlayerForceCommandExecution() : JsonPacket() {

    constructor(cloudPlayer: ICloudPlayer, command: String) : this() {
        this.jsonData.append("playerUniqueId", cloudPlayer.getUniqueId())
                .append("command", command)
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return null
        val command = this.jsonData.getString("command") ?: return null
        CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)?.forceCommandExecution(command)
        return null
    }
}