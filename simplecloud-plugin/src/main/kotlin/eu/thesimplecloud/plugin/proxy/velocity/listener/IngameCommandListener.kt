package eu.thesimplecloud.plugin.proxy.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChatEvent
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.player.CloudPlayerCommandExecuteEvent
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import eu.thesimplecloud.plugin.network.packets.PacketOutPlayerExecuteCommand
import eu.thesimplecloud.plugin.proxy.bungee.CloudBungeePlugin
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
    fun handle(event: PlayerChatEvent) {
        val player = event.player
        if (event.message.startsWith("/")){
            val rawCommand = event.message.replaceFirst("/", "")
            val commandStart = rawCommand.split(" ")[0]
            if (CloudBungeePlugin.instance.synchronizedIngameCommandNamesContainer.names.contains(commandStart.toLowerCase())) {
                CloudPlugin.instance.communicationClient.sendUnitQuery(PacketOutPlayerExecuteCommand(player.getCloudPlayer(), rawCommand))
                event.result = PlayerChatEvent.ChatResult.denied()
            }
            CloudAPI.instance.getEventManager().call(CloudPlayerCommandExecuteEvent(player.uniqueId, player.username, rawCommand))
        }
    }

}