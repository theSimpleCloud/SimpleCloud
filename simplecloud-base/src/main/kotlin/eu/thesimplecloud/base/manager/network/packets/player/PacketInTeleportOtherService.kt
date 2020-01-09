package eu.thesimplecloud.base.manager.network.packets.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.api.exception.NoSuchServiceException
import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.flatten
import java.lang.IllegalArgumentException
import java.util.*
import java.util.concurrent.TimeUnit

class PacketInTeleportOtherService : JsonPacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return contentException("playerUniqueId")
        val serviceName = this.jsonData.getString("serviceName") ?: return contentException("serviceName")
        val simpleLocation = this.jsonData.getObject("simpleLocation", SimpleLocation::class.java) ?: return contentException("simpleLocation")
        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId) ?: return failure(NoSuchPlayerException("Player cannot be found"))
        val service = CloudAPI.instance.getCloudServiceManger().getCloudServiceByName(serviceName) ?: return failure(NoSuchServiceException("Service cannot be found"))
        if (service.isProxy())
            return failure(IllegalArgumentException("Cannot connect a player to a proxy service."))
       return cloudPlayer.teleport(simpleLocation.toServiceLocation(serviceName))
    }
}