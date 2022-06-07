package eu.thesimplecloud.plugin.proxy.velocity.listener

import com.velocitypowered.api.proxy.ProxyServer
import eu.thesimplecloud.api.event.player.CloudPlayerDisconnectEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import net.kyori.adventure.text.Component


class CloudPlayerDisconnectListener(
    private val proxyServer: ProxyServer
): IListener {

    @CloudEventHandler
    fun handle(event: CloudPlayerDisconnectEvent) {
        val playerUniqueId = event.playerUniqueId
        this.proxyServer.allPlayers.forEach {
            if (playerUniqueId == it.uniqueId)
                it.disconnect(Component.text("§cLogin from another session!"))
        }
    }
}