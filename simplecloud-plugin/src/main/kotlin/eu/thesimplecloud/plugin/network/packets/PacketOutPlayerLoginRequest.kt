package eu.thesimplecloud.plugin.network.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

class PacketOutPlayerLoginRequest(val uniqueId: UUID) : ObjectPacket<UUID>(uniqueId) {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> = unit()
}