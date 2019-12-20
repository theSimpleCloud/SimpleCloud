package eu.thesimplecloud.lib.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.player.ICloudPlayer
import eu.thesimplecloud.lib.service.ICloudService
import java.util.*

class PacketIOConnectCloudPlayer() : JsonPacket() {

    constructor(cloudPlayer: ICloudPlayer, service: ICloudService) : this() {
        this.jsonData.append("playerUniqueId", cloudPlayer.getUniqueId()).append("serviceName", service.getName())
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return ObjectPacket.getNewObjectPacketWithContent(false)
        val serviceName = this.jsonData.getString("serviceName") ?: return ObjectPacket.getNewObjectPacketWithContent(false)
        val cloudService = CloudLib.instance.getCloudServiceManger().getCloudService(serviceName) ?: return ObjectPacket.getNewObjectPacketWithContent(false)
        val promise = CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)?.connect(cloudService)
        promise ?: return ObjectPacket.getNewObjectPacketWithContent(false)
        promise.syncUninterruptibly()
        return ObjectPacket.getNewObjectPacketWithContent(promise.isSuccess)
    }
}