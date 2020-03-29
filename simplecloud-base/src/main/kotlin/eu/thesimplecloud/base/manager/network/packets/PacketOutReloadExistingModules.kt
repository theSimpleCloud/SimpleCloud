package eu.thesimplecloud.base.manager.network.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketOutReloadExistingModules: JsonPacket() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        return unit()
    }
}