package eu.thesimplecloud.plugin.proxy.bungee.listener

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.player.CloudPlayerCommandExecuteEvent
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import eu.thesimplecloud.plugin.network.packets.PacketOutGetTabSuggestions
import eu.thesimplecloud.plugin.network.packets.PacketOutPlayerExecuteCommand
import eu.thesimplecloud.plugin.proxy.ProxyEventHandler
import eu.thesimplecloud.plugin.proxy.bungee.CloudBungeePlugin
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.event.TabCompleteEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class IngameCommandListener : Listener {

    @EventHandler
    fun on(event: ChatEvent) {
        if (event.sender !is ProxiedPlayer) return
        val player = event.sender as ProxiedPlayer
        if (event.isCommand){
            val rawCommand = event.message.replaceFirst("/", "")
            val commandStart = rawCommand.split(" ")[0]
            if (CloudBungeePlugin.instance.synchronizedIngameCommandNamesContainer.names.contains(commandStart.toLowerCase())) {
                CloudPlugin.instance.communicationClient.sendUnitQuery(PacketOutPlayerExecuteCommand(player.getCloudPlayer(), rawCommand))
                event.isCancelled = true
            }
            CloudAPI.instance.getEventManager().call(CloudPlayerCommandExecuteEvent(player.uniqueId, player.name, rawCommand))
        }
    }

    @EventHandler
    fun on(event: TabCompleteEvent) {
        val player = event.sender as? ProxiedPlayer?: return

        event.suggestions.addAll(ProxyEventHandler.handleTabComplete(player.uniqueId, event.cursor))
    }

}