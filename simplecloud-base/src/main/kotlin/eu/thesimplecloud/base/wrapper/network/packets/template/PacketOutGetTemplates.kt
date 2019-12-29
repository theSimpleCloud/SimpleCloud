package eu.thesimplecloud.base.wrapper.network.packets.template

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket

class PacketOutGetTemplates : JsonPacket() {
    override suspend fun handle(connection: IConnection) = unit()
}