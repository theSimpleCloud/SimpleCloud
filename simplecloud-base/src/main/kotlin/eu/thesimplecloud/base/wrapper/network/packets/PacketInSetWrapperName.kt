package eu.thesimplecloud.base.wrapper.network.packets

import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.lib.network.packets.wrapper.PacketIOUpdateWrapperInfo
import eu.thesimplecloud.lib.wrapper.IWritableWrapperInfo

class PacketInSetWrapperName : ObjectPacket<String>(String::class.java) {

    override suspend fun handle(connection: IConnection): IPacket? {
        val name = this.value ?: return null
        Wrapper.instance.thisWrapperName = name
        Wrapper.instance.startProcessQueue()
        if (Wrapper.instance.isStartedInManagerDirectory()) {
            val thisWrapper = Wrapper.instance.getThisWrapper()
            thisWrapper as IWritableWrapperInfo
            thisWrapper.setTemplatesReceived(true)
            Wrapper.instance.communicationClient.sendQuery(PacketIOUpdateWrapperInfo(thisWrapper))
        }
        return null
    }
}