package eu.thesimplecloud.api.network.packets.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.function.Predicate

class PacketIOGetOnlinePlayersFiltered() : ObjectPacket<Predicate<ICloudPlayer>>() {

    constructor(predicate: Predicate<ICloudPlayer>) : this() {
        this.value = predicate
    }


    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val value = this.value ?: return contentException("value")
        return CloudAPI.instance.getCloudPlayerManager().getOnlinePlayersFiltered(value).then { it.toTypedArray() }
    }
}