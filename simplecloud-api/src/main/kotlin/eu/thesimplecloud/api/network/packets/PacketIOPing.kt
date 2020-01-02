package eu.thesimplecloud.api.network.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOPing() : ObjectPacket<Long>() {

    constructor(time: Long) : this() {
        this.value = time
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val value = this.value ?: return contentException("value")
        return success(System.currentTimeMillis() - value)
    }

}