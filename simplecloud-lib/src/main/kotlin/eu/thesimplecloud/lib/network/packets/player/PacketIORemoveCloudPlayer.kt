package eu.thesimplecloud.lib.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import java.util.*

class PacketIORemoveCloudPlayer() : ObjectPacket<UUID>() {

    constructor(uniqueId: UUID) : this() {
        this.value = uniqueId
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val value = this.value ?: return contentException("value")
        CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(value)?.let {
            CloudLib.instance.getCloudPlayerManager().removeCloudPlayer(it)
        }
        return unit()
    }
}