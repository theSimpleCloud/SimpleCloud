package eu.thesimplecloud.lib.network.packets.service

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.impl.DefaultCloudService

class PacketIOUpdateCloudService() : ObjectPacket<ICloudService>(DefaultCloudService::class.java) {

    constructor(service: ICloudService) : this() {
        this.value = service
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        this.value?.let { CloudLib.instance.getCloudServiceManger().updateCloudService(it) }
        return null
    }


}