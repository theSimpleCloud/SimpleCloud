package eu.thesimplecloud.plugin.network.packets

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.04.2020
 * Time: 19:05
 */
class PacketOutGetTabSuggestions(): JsonPacket() {

    constructor(uuid: UUID, command: String): this() {
        this.jsonData
                .append("uuid", uuid)
                .append("command", command)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        return unit()
    }

}