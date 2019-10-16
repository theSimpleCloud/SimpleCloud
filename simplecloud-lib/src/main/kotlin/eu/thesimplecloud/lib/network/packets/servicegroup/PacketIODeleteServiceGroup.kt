package eu.thesimplecloud.lib.network.packets.servicegroup

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.network.reponsehandler.CloudServiceGroupResponseHandler

class PacketIODeleteServiceGroup() : ObjectPacket<String>(String::class.java) {

    constructor(name: String) : this() {
        this.value = name
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val name = this.value ?: return getNewObjectPacketWithContent(false)
        val serviceGroup = CloudLib.instance.getCloudServiceGroupManager().getServiceGroup(name) ?: return getNewObjectPacketWithContent(false)
        val deleteServiceGroupPromise = CloudLib.instance.getCloudServiceGroupManager().deleteServiceGroup(serviceGroup)
        deleteServiceGroupPromise.syncUninterruptibly()
        return getNewObjectPacketWithContent(deleteServiceGroupPromise.isSuccess)
    }
}