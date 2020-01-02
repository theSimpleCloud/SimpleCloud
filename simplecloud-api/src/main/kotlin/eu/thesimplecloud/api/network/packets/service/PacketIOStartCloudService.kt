package eu.thesimplecloud.api.network.packets.service

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ICloudService

class PacketIOStartCloudService(): ObjectPacket<String>() {

    constructor(groupName: String) :this() {
        this.value = groupName
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<ICloudService> {
        val value = this.value ?: return contentException("value")
        return CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(value)?.startNewService() ?: failure(NoSuchElementException("Group does not exist"))
    }
}