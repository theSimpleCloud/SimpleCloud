package eu.thesimplecloud.api.network.packets.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.exception.UnreachableServiceException
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*
import kotlin.NoSuchElementException

class PacketIOConnectCloudPlayer() : JsonPacket() {

    constructor(cloudPlayer: ICloudPlayer, service: ICloudService) : this() {
        this.jsonData.append("playerUniqueId", cloudPlayer.getUniqueId()).append("serviceName", service.getName())
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return contentException("playerUniqueId")
        val serviceName = this.jsonData.getString("serviceName") ?: return contentException("serviceName")
        val cloudService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(serviceName) ?: return failure(UnreachableServiceException(""))
        return CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)?.connect(cloudService) ?: return failure(NoSuchElementException("Player not found"))
    }
}