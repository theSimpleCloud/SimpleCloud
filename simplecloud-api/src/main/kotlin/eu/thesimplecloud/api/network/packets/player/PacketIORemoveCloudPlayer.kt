package eu.thesimplecloud.api.network.packets.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

class PacketIORemoveCloudPlayer() : ObjectPacket<UUID>() {

    constructor(uniqueId: UUID) : this() {
        this.value = uniqueId
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val value = this.value ?: return contentException("value")
        CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(value)?.let {
            CloudAPI.instance.getCloudPlayerManager().removeCloudPlayer(it)
        }
        return unit()
    }
}