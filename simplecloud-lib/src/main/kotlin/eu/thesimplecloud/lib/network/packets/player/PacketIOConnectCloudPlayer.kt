package eu.thesimplecloud.lib.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.exception.UnavailableServiceException
import eu.thesimplecloud.lib.player.ICloudPlayer
import eu.thesimplecloud.lib.service.ICloudService
import java.util.*
import kotlin.NoSuchElementException

class PacketIOConnectCloudPlayer() : JsonPacket() {

    constructor(cloudPlayer: ICloudPlayer, service: ICloudService) : this() {
        this.jsonData.append("playerUniqueId", cloudPlayer.getUniqueId()).append("serviceName", service.getName())
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return contentException("playerUniqueId")
        val serviceName = this.jsonData.getString("serviceName") ?: return contentException("serviceName")
        val cloudService = CloudLib.instance.getCloudServiceManger().getCloudService(serviceName) ?: return failure(UnavailableServiceException(""))
        return CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)?.connect(cloudService) ?: return failure(NoSuchElementException("Player not found"))
    }
}