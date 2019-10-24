package eu.thesimplecloud.base.manager.network.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket

class PacketOutSetWrapperName() : ObjectPacket<String>(String::class.java) {

    constructor(name: String): this() {
        this.value = name
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        return null
    }
}