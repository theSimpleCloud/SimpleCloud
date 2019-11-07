package eu.thesimplecloud.lib.network.packets.service

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.lib.CloudLib

class PacketIOStartCloudService(): ObjectPacket<String>(String::class.java) {

    constructor(groupName: String) :this() {
        this.value = groupName
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val promise = this.value?.let { CloudLib.instance.getCloudServiceGroupManager().getServiceGroup(it)?.startNewService() }
        promise ?: return null
        promise.syncUninterruptibly()
        return getNewObjectPacketWithContent(promise.get())
    }
}