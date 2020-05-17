package eu.thesimplecloud.plugin.proxy.bungee

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.ReconnectHandler
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer

class ReconnectHandlerImpl : ReconnectHandler {
    override fun save() {
    }

    override fun getServer(player: ProxiedPlayer): ServerInfo {
        return ProxyServer.getInstance().getServerInfo("fallback")
    }

    override fun setServer(player: ProxiedPlayer?) {
    }

    override fun close() {
    }
}