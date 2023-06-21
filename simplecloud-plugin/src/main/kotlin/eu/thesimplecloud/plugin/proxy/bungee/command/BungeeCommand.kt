package eu.thesimplecloud.plugin.proxy.bungee.command

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.player.CloudPlayerCommandExecuteEvent
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import eu.thesimplecloud.plugin.network.packets.PacketOutPlayerExecuteCommand
import eu.thesimplecloud.plugin.proxy.ProxyEventHandler
import eu.thesimplecloud.plugin.proxy.bungee.CloudBungeePlugin
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.TabExecutor
import java.util.concurrent.CompletableFuture

/**
 * Created by MrManHD
 * Class create at 20.06.2023 15:44
 */

class BungeeCommand(
    val commandStart: String
) : Command(commandStart), TabExecutor {

    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (sender !is ProxiedPlayer)
            return

        val rawCommand = "$commandStart " + args.joinToString(" ")

        if (CloudBungeePlugin.instance.synchronizedIngameCommandsProperty.getValue()
                .contains(this.commandStart.lowercase())
        ) {
            CloudPlugin.instance.connectionToManager.sendUnitQuery(
                PacketOutPlayerExecuteCommand(
                    sender.getCloudPlayer(),
                    rawCommand
                )
            )
        }
        CloudAPI.instance.getEventManager()
            .call(CloudPlayerCommandExecuteEvent(sender.uniqueId, sender.name, rawCommand))
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): MutableIterable<String> {
        if (sender !is ProxiedPlayer)
            return mutableListOf()
        val rawCommand = "$commandStart " + args.joinToString(" ")
        return getSuggestCompletableFuture(sender, rawCommand).get().sorted().toMutableList()
    }

    private fun getSuggestCompletableFuture(
        player: ProxiedPlayer,
        rawCommand: String
    ): CompletableFuture<MutableList<String>> {
        val completableFuture = CompletableFuture<MutableList<String>>()
        ProxyEventHandler.handleTabComplete(player.uniqueId, rawCommand)
            .addResultListener { completableFuture.complete(it.toMutableList()) }
        return completableFuture
    }
}