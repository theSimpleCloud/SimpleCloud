package eu.thesimplecloud.lib.network.packets.wrapper

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.wrapper.IWrapperInfo
import eu.thesimplecloud.lib.wrapper.impl.DefaultWrapperInfo

class PacketIOWrapperInfo() : ObjectPacket<IWrapperInfo>(DefaultWrapperInfo::class.java) {

    constructor(wrapperInfo: IWrapperInfo) : this() {
        this.value = wrapperInfo
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val wrapperInfo = this.value ?: return null
        CloudLib.instance.getWrapperManager().updateWrapper(wrapperInfo)
        return null
    }
}