package eu.thesimplecloud.api.network.packets.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOGetAllOnlinePlayers() : ObjectPacket<Unit>() {



    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        return CloudAPI.instance.getCloudPlayerManager().getAllOnlinePlayers().then { it.toTypedArray() }
    }
}