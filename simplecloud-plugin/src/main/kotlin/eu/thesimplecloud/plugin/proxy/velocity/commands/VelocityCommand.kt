package eu.thesimplecloud.plugin.proxy.velocity.commands

import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.player.CloudPlayerCommandExecuteEvent
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import eu.thesimplecloud.plugin.network.packets.PacketOutPlayerExecuteCommand
import eu.thesimplecloud.plugin.proxy.ProxyEventHandler
import eu.thesimplecloud.plugin.proxy.velocity.CloudVelocityPlugin
import eu.thesimplecloud.plugin.startup.CloudPlugin

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 19.05.2020
 * Time: 20:08
 */
class VelocityCommand(private val commandStart: String) : Command {

    override fun execute(source: CommandSource?, args: Array<out String>) {
        val player = source as? Player?: return

        val command = "$commandStart " + args.joinToString(" ").trim()

        if (CloudVelocityPlugin.instance.synchronizedIngameCommandNamesContainer.names.contains(commandStart.toLowerCase())) {
            CloudPlugin.instance.communicationClient.sendUnitQuery(PacketOutPlayerExecuteCommand(player.getCloudPlayer(), command))
        }
        CloudAPI.instance.getEventManager().call(CloudPlayerCommandExecuteEvent(player.uniqueId, player.username, command))
    }

    override fun suggest(source: CommandSource?, args: Array<out String>): MutableList<String> {
        val player = source as? Player?: return super.suggest(source, args)
        val rawCommand = listOf(commandStart).union(args.toList()).joinToString(" ")
        return ProxyEventHandler.handleTabComplete(player.uniqueId, rawCommand).toMutableList()
    }

}