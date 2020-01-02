package eu.thesimplecloud.api.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.player.text.CloudText
import java.util.*

class PacketIOSendMessageToCloudPlayer() : JsonPacket() {

    constructor(cloudPlayer: ICloudPlayer, cloudText: CloudText) : this() {
        this.jsonData.append("playerUniqueId", cloudPlayer.getUniqueId()).append("cloudText", cloudText)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return contentException("playerUniqueId")
        val cloudText = this.jsonData.getObject("cloudText", CloudText::class.java) ?: return contentException("cloudText")
        CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)?.sendMessage(cloudText)
        return unit()
    }
}