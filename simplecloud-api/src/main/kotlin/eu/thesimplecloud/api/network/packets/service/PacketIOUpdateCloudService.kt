package eu.thesimplecloud.api.network.packets.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOUpdateCloudService() : ObjectPacket<ICloudService>() {


    constructor(service: ICloudService) : this() {
        this.value = service
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val value = this.value ?: return contentException("value")
        CloudAPI.instance.getCloudServiceManager().updateCloudService(value, fromPacket = true)
        return unit()
    }


}