package eu.thesimplecloud.lib.network.packets.service

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import java.lang.IllegalStateException

class PacketIOWrapperStartService() : ObjectPacket<String>() {

    constructor(name: String): this() {
        this.value = name
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val name = this.value ?: return contentException("value")
        val cloudService = CloudLib.instance.getCloudServiceManger().getCloudServiceByName(name)
        cloudService ?: throw IllegalStateException("Service to start was null. Name: $name")
        cloudService.start()
        return unit()
    }
}