package eu.thesimplecloud.api.network.packets.wrapper

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.wrapper.IWrapperInfo

class PacketIOUpdateWrapperInfo() : ObjectPacket<IWrapperInfo>() {

    constructor(wrapperInfo: IWrapperInfo) : this() {
        this.value = wrapperInfo
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val wrapperInfo = this.value ?: return contentException("value")
        CloudAPI.instance.getWrapperManager().updateWrapper(wrapperInfo)
        return unit()
    }
}