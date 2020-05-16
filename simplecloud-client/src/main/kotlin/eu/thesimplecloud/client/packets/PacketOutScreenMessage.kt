package eu.thesimplecloud.client.packets

import eu.thesimplecloud.api.client.NetworkComponentType
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket

class PacketOutScreenMessage(cloudClientType: NetworkComponentType, commandExecutable: ICommandExecutable, message: String) : JsonPacket() {

    init {
        this.jsonData.append("cloudClientType", cloudClientType).append("name", commandExecutable.getName()).append("message", message)
    }

    override suspend fun handle(connection: IConnection) = unit()

}