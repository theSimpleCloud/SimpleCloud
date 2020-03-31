package eu.thesimplecloud.api.network.packets.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

class PacketIOPlayerHasPermission() : JsonPacket() {

    constructor(uniqueId: UUID, permission: String) : this() {
        this.jsonData.append("uniqueId", uniqueId).append("permission", permission)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Boolean> {
        val uniqueId = this.jsonData.getObject("uniqueId", UUID::class.java) ?: return contentException("uniqueId")
        val permission = this.jsonData.getString("permission") ?: return contentException("permission")
        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(uniqueId)
        cloudPlayer ?: return failure(NoSuchPlayerException("Player cannot be found"))
        return cloudPlayer.hasPermission(permission)
    }
}