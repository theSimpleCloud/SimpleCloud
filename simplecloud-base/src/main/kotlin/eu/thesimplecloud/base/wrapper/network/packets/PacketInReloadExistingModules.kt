package eu.thesimplecloud.base.wrapper.network.packets

import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketInReloadExistingModules : JsonPacket() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        Wrapper.instance.reloadExistingModules()
        return unit()
    }
}