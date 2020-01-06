package eu.thesimplecloud.api.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer

class PacketIOUpdateCloudPlayer() : ObjectPacket<ICloudPlayer>() {

    constructor(cloudPlayer: ICloudPlayer) : this() {
        this.value = cloudPlayer
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        this.value?.let { CloudAPI.instance.getCloudPlayerManager().updateCloudPlayer(it, fromPacket = true) }
        return unit()
    }
}