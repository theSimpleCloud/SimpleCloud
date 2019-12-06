package eu.thesimplecloud.lib.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.player.ICloudPlayer
import eu.thesimplecloud.lib.player.text.CloudText
import java.util.*

class PacketIOSendMessageToCloudPlayer() : JsonPacket() {

    constructor(cloudPlayer: ICloudPlayer, cloudText: CloudText) : this() {
        this.jsonData.append("playerUniqueId", cloudPlayer.getUniqueId()).append("cloudText", cloudText)
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return null
        val cloudText = this.jsonData.getObject("cloudText", CloudText::class.java) ?: return null
        CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)?.sendMessage(cloudText)
        return null
    }
}