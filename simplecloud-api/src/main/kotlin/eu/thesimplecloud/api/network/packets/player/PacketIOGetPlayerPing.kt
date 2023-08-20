package eu.thesimplecloud.api.network.packets.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

/**
 * Created by MrManHD
 * Class create at 20.06.2023 19:48
 */

class PacketIOGetPlayerPing() : ObjectPacket<UUID>() {

    constructor(cloudPlayer: ICloudPlayer) : this() {
        this.value = cloudPlayer.getUniqueId()
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        val value = this.value ?: return contentException("value")
        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(value)
            ?: return failure(NoSuchElementException("Player does not exist"))
        return cloudPlayer.getPing()
    }

}