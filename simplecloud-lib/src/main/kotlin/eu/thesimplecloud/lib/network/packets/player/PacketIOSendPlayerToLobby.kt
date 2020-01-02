package eu.thesimplecloud.lib.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.exception.NoSuchPlayerException
import java.util.*

class PacketIOSendPlayerToLobby() : ObjectPacket<UUID>() {

    constructor(uniqueId: UUID) : this() {
        this.value = uniqueId
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val value = this.value ?: return contentException("value")
        val cloudPlayer = CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(value)
        cloudPlayer ?: return failure(NoSuchPlayerException("Player cannot be found"))
        return cloudPlayer.sendToLobby()
    }
}