package eu.thesimplecloud.plugin.network.packets

import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

class PacketOutTeleportOtherService(playerUniqueId: UUID, serviceName: String, simpleLocation: SimpleLocation) : JsonPacket() {

    init {
        this.jsonData.append("playerUniqueId", playerUniqueId).append("serviceName", serviceName).append("simpleLocation", simpleLocation)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> = unit()
}