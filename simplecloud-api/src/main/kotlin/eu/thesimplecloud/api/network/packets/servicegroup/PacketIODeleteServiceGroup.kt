package eu.thesimplecloud.api.network.packets.servicegroup

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIODeleteServiceGroup() : ObjectPacket<String>() {

    constructor(name: String) : this() {
        this.value = name
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val name = this.value ?: return contentException("value")
        val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(name) ?: return failure(NoSuchElementException("Group does not exist"))
        return CloudAPI.instance.getCloudServiceGroupManager().deleteServiceGroup(serviceGroup)
    }
}