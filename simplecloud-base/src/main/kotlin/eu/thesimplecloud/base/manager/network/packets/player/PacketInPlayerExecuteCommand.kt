package eu.thesimplecloud.base.manager.network.packets.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.launcher.startup.Launcher
import java.util.*

class PacketInPlayerExecuteCommand() : JsonPacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val playerUniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return contentException("playerUniqueId")
        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId) ?: return failure(NoSuchPlayerException("Cannot find player by UUID: $playerUniqueId"))
        val command = this.jsonData.getString("command") ?: return contentException("command")
        try {
            Launcher.instance.commandManager.handleCommand(command, cloudPlayer)
        } catch (ex: Exception) {
            cloudPlayer.sendMessage("§cAn error occurred while executing the command.")
            throw ex
        }
        return unit()
    }
}