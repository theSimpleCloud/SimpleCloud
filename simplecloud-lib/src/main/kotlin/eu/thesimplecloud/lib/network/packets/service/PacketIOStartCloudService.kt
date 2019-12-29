package eu.thesimplecloud.lib.network.packets.service

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.service.ICloudService

class PacketIOStartCloudService(): ObjectPacket<String>() {

    constructor(groupName: String) :this() {
        this.value = groupName
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<ICloudService> {
        val value = this.value ?: return contentException("value")
        return CloudLib.instance.getCloudServiceGroupManager().getServiceGroup(value)?.startNewService() ?: failure(NoSuchElementException("Group does not exist"))
    }
}