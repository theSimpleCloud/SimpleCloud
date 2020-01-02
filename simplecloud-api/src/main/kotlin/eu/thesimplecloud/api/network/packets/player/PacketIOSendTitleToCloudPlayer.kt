package eu.thesimplecloud.api.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer
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

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return contentException("playerUniqueId")
        val title = this.jsonData.getString("title") ?: return contentException("title")
        val subTitle = this.jsonData.getString("subTitle") ?: return contentException("subTitle")
        val fadeIn = this.jsonData.getInt("fadeIn") ?: return contentException("fadeIn")
        val stay = this.jsonData.getInt("stay") ?: return contentException("stay")
        val fadeOut = this.jsonData.getInt("fadeOut") ?: return contentException("fadeOut")
        CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)?.sendTitle(title, subTitle, fadeIn, stay, fadeOut)
        return unit()
    }
}