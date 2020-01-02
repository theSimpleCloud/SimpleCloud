package eu.thesimplecloud.client.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.api.client.CloudClientType
import eu.thesimplecloud.api.screen.ICommandExecutable

class PacketOutScreenMessage(cloudClientType: CloudClientType, commandExecutable: ICommandExecutable, message: String) : JsonPacket() {

    init {
        this.jsonData.append("cloudClientType", cloudClientType).append("name", commandExecutable.getName()).append("message", message)
    }

    override suspend fun handle(connection: IConnection) = unit()

}