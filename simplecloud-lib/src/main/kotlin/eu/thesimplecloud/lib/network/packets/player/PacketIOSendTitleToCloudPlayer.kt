package eu.thesimplecloud.lib.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.player.ICloudPlayer
import eu.thesimplecloud.lib.service.ICloudService
import java.util.*

class PacketIOSendTitleToCloudPlayer() : JsonPacket() {

    constructor(cloudPlayer: ICloudPlayer, title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) : this() {
        this.jsonData.append("playerUniqueId", cloudPlayer.getUniqueId())
                .append("title", title)
                .append("subTitle", subTitle)
                .append("fadeIn", fadeIn)
                .append("stay", stay)
                .append("fadeOut", fadeOut)
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return null
        val title = this.jsonData.getString("title") ?: return null
        val subTitle = this.jsonData.getString("subTitle") ?: return null
        val fadeIn = this.jsonData.getInt("fadeIn") ?: return null
        val stay = this.jsonData.getInt("stay") ?: return null
        val fadeOut = this.jsonData.getInt("fadeOut") ?: return null
        CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)?.sendTitle(title, subTitle, fadeIn, stay, fadeOut)
        return null
    }
}