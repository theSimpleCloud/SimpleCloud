package eu.thesimplecloud.lib.network.packets.player

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.player.CloudPlayer
import eu.thesimplecloud.lib.player.ICloudPlayer

class PacketIOUpdateCloudPlayer() : ObjectPacket<ICloudPlayer>() {

    constructor(cloudPlayer: ICloudPlayer) : this() {
        this.value = cloudPlayer
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        this.value?.let { CloudLib.instance.getCloudPlayerManager().updateCloudPlayer(it) }
        return unit()
    }
}