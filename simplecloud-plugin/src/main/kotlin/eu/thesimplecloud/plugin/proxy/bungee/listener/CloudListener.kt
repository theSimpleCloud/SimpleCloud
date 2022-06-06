package eu.thesimplecloud.plugin.proxy.bungee.listener

import eu.thesimplecloud.api.event.player.CloudPlayerDisconnectEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.plugin.proxy.bungee.CloudBungeePlugin
import eu.thesimplecloud.plugin.proxy.velocity.CloudVelocityPlugin
import net.kyori.adventure.text.Component

class CloudListener: IListener {

    @CloudEventHandler
    fun handle(event: CloudPlayerDisconnectEvent) {
        val playerUniqueId = event.playerUniqueId
        CloudBungeePlugin.instance.proxy.players.map {
            if (playerUniqueId == it.uniqueId)
                it.disconnect("§cLogin from another session!")
        }
    }
}