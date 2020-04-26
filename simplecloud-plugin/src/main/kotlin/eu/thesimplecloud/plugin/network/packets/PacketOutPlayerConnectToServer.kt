package eu.thesimplecloud.plugin.network.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

class PacketOutPlayerConnectToServer(playerUniqueId: UUID, serviceName: String) : JsonPacket() {

    init {
        this.jsonData.append("playerUniqueId", playerUniqueId).append("serviceName", serviceName)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> = unit()
}