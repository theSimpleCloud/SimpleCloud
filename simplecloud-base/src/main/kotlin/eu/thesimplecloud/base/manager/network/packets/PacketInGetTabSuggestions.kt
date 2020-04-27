package eu.thesimplecloud.base.manager.network.packets

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.launcher.startup.Launcher
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.04.2020
 * Time: 19:05
 */
class PacketInGetTabSuggestions(): JsonPacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val command = this.jsonData.getString("command")?: return contentException("command")
        val uuid = this.jsonData.getObject("uuid", UUID::class.java)?: return contentException("uuid")

        val player = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(uuid)?: return CommunicationPromise.failed(NoSuchPlayerException(uuid.toString()))

        return CommunicationPromise.of(Launcher.instance.commandManager.getAvailableTabCompleteArgs(command, player).toTypedArray())
    }

}