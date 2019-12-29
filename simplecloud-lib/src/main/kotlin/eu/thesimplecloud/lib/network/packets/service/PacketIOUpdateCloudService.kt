package eu.thesimplecloud.lib.network.packets.service

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.impl.DefaultCloudService
import java.io.File

class PacketIOUpdateCloudService() : ObjectPacket<ICloudService>() {


    constructor(service: ICloudService) : this() {
        this.value = service
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val value = this.value ?: return contentException("value")
        CloudLib.instance.getCloudServiceManger().updateCloudService(value)
        return unit()
    }


}