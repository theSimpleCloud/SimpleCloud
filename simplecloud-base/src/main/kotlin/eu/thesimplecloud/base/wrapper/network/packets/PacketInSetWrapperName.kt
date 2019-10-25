package eu.thesimplecloud.base.wrapper.network.packets

import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket

class PacketInSetWrapperName : ObjectPacket<String>(String::class.java) {

    override suspend fun handle(connection: IConnection): IPacket? {
        val name = this.value ?: return null
        Wrapper.instance.thisWrapperName = name
        Wrapper.instance.startProcessQueue()
        return null
    }
}