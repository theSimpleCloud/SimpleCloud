package eu.thesimplecloud.client.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.lib.client.CloudClientType
import eu.thesimplecloud.lib.screen.ICommandExecutable

class PacketOutScreenMessage(cloudClientType: CloudClientType, commandExecutable: ICommandExecutable, message: String) : JsonPacket() {

    init {
        this.jsonData.append("cloudClientType", cloudClientType).append("name", commandExecutable.getName()).append("message", message)
    }

    override suspend fun handle(connection: IConnection) = unit()

}