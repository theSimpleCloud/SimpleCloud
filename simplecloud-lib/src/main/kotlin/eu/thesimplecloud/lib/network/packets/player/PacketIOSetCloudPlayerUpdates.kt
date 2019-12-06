package eu.thesimplecloud.lib.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.player.ICloudPlayer
import java.util.*

class PacketIOSetCloudPlayerUpdates() : JsonPacket() {

    constructor(cloudPlayer: ICloudPlayer, updates: Boolean, serviceName: String) : this() {
        this.jsonData.append("playerUniqueId", cloudPlayer.getUniqueId())
                .append("updates", updates).append("serviceName", serviceName)
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return null
        val updates = this.jsonData.getBoolean("updates") ?: return null
        val serviceName = this.jsonData.getString("serviceName") ?: return null
        val cachedCloudPlayer = CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)
        cachedCloudPlayer?.let { CloudLib.instance.getCloudPlayerManager().setUpdates(cachedCloudPlayer, updates, serviceName) }
        return null
    }

}