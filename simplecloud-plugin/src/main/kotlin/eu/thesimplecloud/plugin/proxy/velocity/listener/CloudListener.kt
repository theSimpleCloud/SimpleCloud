package eu.thesimplecloud.plugin.proxy.velocity.listener

import eu.thesimplecloud.api.event.player.CloudPlayerDisconnectEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.plugin.proxy.velocity.CloudVelocityPlugin
import net.kyori.adventure.text.Component


class CloudListener: IListener {

    @CloudEventHandler
    fun handle(event: CloudPlayerDisconnectEvent) {
        val playerUniqueId = event.playerUniqueId
        CloudVelocityPlugin.instance.proxyServer.allPlayers.map {
            if (playerUniqueId == it.uniqueId)
                it.disconnect(Component.text("§cLogin from another session!"))
        }
    }
}