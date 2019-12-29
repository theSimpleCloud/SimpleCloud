package eu.thesimplecloud.lib.network.packets.wrapper

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.wrapper.IWrapperInfo
import eu.thesimplecloud.lib.wrapper.impl.DefaultWrapperInfo

class PacketIOUpdateWrapperInfo() : ObjectPacket<IWrapperInfo>() {

    constructor(wrapperInfo: IWrapperInfo) : this() {
        this.value = wrapperInfo
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val wrapperInfo = this.value ?: return contentException("value")
        CloudLib.instance.getWrapperManager().updateWrapper(wrapperInfo)
        return unit()
    }
}