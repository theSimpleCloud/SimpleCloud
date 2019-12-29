package eu.thesimplecloud.lib.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.player.ICloudPlayer
import java.util.*

class PacketIOSetCloudPlayerUpdates() : JsonPacket() {

    constructor(cloudPlayer: ICloudPlayer, updates: Boolean, serviceName: String) : this() {
        this.jsonData.append("playerUniqueId", cloudPlayer.getUniqueId())
                .append("updates", updates).append("serviceName", serviceName)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return contentException("playerUniqueId")
        val updates = this.jsonData.getBoolean("updates") ?: return contentException("updates")
        val serviceName = this.jsonData.getString("serviceName") ?: return contentException("serviceName")
        val cachedCloudPlayer = CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)
        cachedCloudPlayer?.let { CloudLib.instance.getCloudPlayerManager().setUpdates(cachedCloudPlayer, updates, serviceName) }
        return unit()
    }

}