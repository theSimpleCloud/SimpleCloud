package eu.thesimplecloud.api.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.api.player.ICloudPlayer
import java.util.*
import kotlin.NoSuchElementException

class PacketIOTeleportPlayer() : JsonPacket() {

    constructor(cloudPlayer: ICloudPlayer, simpleLocation: SimpleLocation): this() {
        this.jsonData.append("playerUniqueId", cloudPlayer.getUniqueId())
                .append("simpleLocation", simpleLocation)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return contentException("playerUniqueId")
        val simpleLocation = this.jsonData.getObject("simpleLocation", SimpleLocation::class.java) ?: return contentException("simpleLocation")
        val cachedCloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)
        cachedCloudPlayer ?: return failure(NoSuchElementException("Player does not exist"))
        cachedCloudPlayer.teleport(simpleLocation)
        return unit()
    }
}