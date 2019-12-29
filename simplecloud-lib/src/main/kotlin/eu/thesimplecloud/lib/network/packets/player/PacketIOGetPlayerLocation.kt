package eu.thesimplecloud.lib.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.player.ICloudPlayer
import java.util.*
import kotlin.NoSuchElementException

class PacketIOGetPlayerLocation() : ObjectPacket<UUID>() {

    constructor(cloudPlayer: ICloudPlayer) : this() {
        this.value = cloudPlayer.getUniqueId()
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val value = this.value ?: return contentException("value")
        val cloudPlayer = CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(value)
                ?: return failure(NoSuchElementException("Player does not exist"))
        return cloudPlayer.getLocation()
    }

}