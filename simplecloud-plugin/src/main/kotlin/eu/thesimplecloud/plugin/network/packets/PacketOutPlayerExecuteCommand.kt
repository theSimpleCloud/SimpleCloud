package eu.thesimplecloud.plugin.network.packets

import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketOutPlayerExecuteCommand() : JsonPacket() {

    constructor(cloudPlayer: ICloudPlayer, command: String) : this() {
        this.jsonData.append("playerUniqueId", cloudPlayer.getUniqueId()).append("command", command)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        return unit()
    }
}