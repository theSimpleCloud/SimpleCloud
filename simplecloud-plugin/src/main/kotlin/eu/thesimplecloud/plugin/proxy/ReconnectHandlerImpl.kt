package eu.thesimplecloud.plugin.proxy

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.ReconnectHandler
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer

class ReconnectHandlerImpl : ReconnectHandler {
    override fun save() {
    }

    override fun getServer(player: ProxiedPlayer): ServerInfo? {
        return CloudProxyPlugin.instance.lobbyConnector.getLobbyServer(player)
    }

    override fun setServer(player: ProxiedPlayer?) {
    }

    override fun close() {
    }
}