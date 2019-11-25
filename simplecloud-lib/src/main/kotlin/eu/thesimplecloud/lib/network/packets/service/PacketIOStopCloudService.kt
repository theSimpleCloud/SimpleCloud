package eu.thesimplecloud.lib.network.packets.service

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.lib.CloudLib

class PacketIOStopCloudService() : ObjectPacket<String>(String::class.java) {

    constructor(name: String) : this() {
        this.value = name
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val name = this.value ?: return getNewObjectPacketWithContent(false)
        val cloudService = CloudLib.instance.getCloudServiceManger().getCloudService(name)
        cloudService?.shutdown()
        return getNewObjectPacketWithContent(cloudService != null && cloudService.isActive())
    }
}