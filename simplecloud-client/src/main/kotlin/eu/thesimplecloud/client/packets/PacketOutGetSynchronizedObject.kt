package eu.thesimplecloud.client.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketOutGetSynchronizedObject(name: String) : ObjectPacket<String>() {

    init {
        this.value = name
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        return unit()
    }
}