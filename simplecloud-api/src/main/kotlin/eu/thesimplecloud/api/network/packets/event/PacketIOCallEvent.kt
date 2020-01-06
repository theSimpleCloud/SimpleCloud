package eu.thesimplecloud.api.network.packets.event

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.eventapi.ISynchronizedEvent
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOCallEvent() : ObjectPacket<ISynchronizedEvent>() {

    constructor(synchronizedEvent: ISynchronizedEvent) : this() {
        this.value = synchronizedEvent
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val value = this.value ?: return contentException("value")
        CloudAPI.instance.getEventManager().call(value, fromPacket = true)
        return unit()
    }
}