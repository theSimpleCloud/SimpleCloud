package eu.thesimplecloud.plugin.proxy.bungee.listener

import eu.thesimplecloud.api.event.player.CloudPlayerDisconnectEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import net.md_5.bungee.api.ProxyServer

class CloudPlayerDisconnectListener(
    private val proxyServer: ProxyServer
): IListener {

    @CloudEventHandler
    fun handle(event: CloudPlayerDisconnectEvent) {
        val playerUniqueId = event.playerUniqueId
        this.proxyServer.players.forEach {
            if (playerUniqueId == it.uniqueId)
                it.disconnect("§cLogin from another session!")
        }
    }
}