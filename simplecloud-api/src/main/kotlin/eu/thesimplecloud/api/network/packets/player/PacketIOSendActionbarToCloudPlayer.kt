package eu.thesimplecloud.api.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer
import java.util.*

class PacketIOSendActionbarToCloudPlayer() : JsonPacket() {

    constructor(cloudPlayer: ICloudPlayer, actionbar: String) : this() {
        this.jsonData.append("playerUniqueId", cloudPlayer.getUniqueId())
                .append("actionbar", actionbar)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return contentException("playerUniqueId")
        val actionbar = this.jsonData.getString("actionbar") ?: return contentException("actionbar")
        CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)?.sendActionBar(actionbar)
        return unit()
    }


}