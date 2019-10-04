package eu.thesimplecloud.lib.packets.service

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.ICloudLib
import eu.thesimplecloud.lib.service.ICloudService

class PacketIOUpdateCloudService() : ObjectPacket<ICloudService>(CloudLib.instance.getCloudServiceImplementationClass()) {

    override suspend fun handle(connection: IConnection): IPacket? {
    }
}