package eu.thesimplecloud.plugin.proxy.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.command.CommandExecuteEvent
import com.velocitypowered.api.event.player.TabCompleteEvent
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
 * Date: 15.05.2020
 * Time: 22:43
 */
class IngameCommandListener(val plugin: CloudVelocityPlugin) {

    @Subscribe
    fun handle(event: CommandExecuteEvent) {
        val player = (event.commandSource as? Player ?: return)

        val command = event.command

        val commandStart = command.split(" ")[0]

        if (CloudVelocityPlugin.instance.synchronizedIngameCommandNamesContainer.names.contains(commandStart.toLowerCase())) {
            CloudPlugin.instance.communicationClient.sendUnitQuery(PacketOutPlayerExecuteCommand(player.getCloudPlayer(), command))
            event.result = CommandExecuteEvent.CommandResult.allowed()
        }
        CloudAPI.instance.getEventManager().call(CloudPlayerCommandExecuteEvent(player.uniqueId, player.username, command))
    }

    @Subscribe
    fun handle(event: TabCompleteEvent) {
        val player = event.player

        event.suggestions.addAll(ProxyEventHandler.handleTabComplete(player.uniqueId, event.partialMessage))
    }

}