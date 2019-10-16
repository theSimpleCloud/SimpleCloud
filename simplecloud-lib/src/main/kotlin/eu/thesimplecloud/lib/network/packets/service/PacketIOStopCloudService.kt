package eu.thesimplecloud.lib.network.packets.service

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.lib.CloudLib

class PacketIOStopCloudService() : ObjectPacket<String>(String::class.java) {

    constructor(name: String) : this(){
        this.value = name
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val value = this.value ?: return null
        val cloudServiceManger = CloudLib.instance.getCloudServiceManger()
        val cloudService = cloudServiceManger.getCloudService(value) ?: return null
        cloudServiceManger.startService(cloudService)
        return null
    }
}