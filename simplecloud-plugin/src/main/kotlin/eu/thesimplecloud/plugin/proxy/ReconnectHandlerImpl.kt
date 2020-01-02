package eu.thesimplecloud.plugin.proxy

import eu.thesimplecloud.api.CloudAPI
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.ReconnectHandler
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer

class ReconnectHandlerImpl : ReconnectHandler {
    override fun save() {
    }

    override fun getServer(player: ProxiedPlayer): ServerInfo? {
        val lobbyGroups = CloudAPI.instance.getCloudServiceGroupManager().getLobbyGroups()
        val sortedLobbyGroups = lobbyGroups.sortedBy { it.getPriority() }.filter { !it.isInMaintenance() || player.hasPermission("cloud.maintenance.join") }
        val groups = sortedLobbyGroups.filter { it.getPermission() == null || player.hasPermission(it.getPermission()) }
        val serviceToConnectTo = groups.map { group -> group.getAllRunningServices().filter { it.isJoinable() }.filter { !it.isFull() } }.flatten().firstOrNull()
        if (serviceToConnectTo == null) println("WARNING: Lobby server was null.")
        return serviceToConnectTo?.let { ProxyServer.getInstance().getServerInfo(it.getName()) }
    }

    override fun setServer(player: ProxiedPlayer?) {
    }

    override fun close() {
    }
}