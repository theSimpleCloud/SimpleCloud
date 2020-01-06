package eu.thesimplecloud.plugin.proxy

import eu.thesimplecloud.api.CloudAPI
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer

class LobbyConnector {

    fun getLobbyServer(player: ProxiedPlayer, filterServices: List<String> = emptyList()): ServerInfo? {
        val lobbyGroups = CloudAPI.instance.getCloudServiceGroupManager().getLobbyGroups()
        val sortedLobbyGroups = lobbyGroups.sortedBy { it.getPriority() }.filter { !it.isInMaintenance() || player.hasPermission("cloud.maintenance.join") }
        val groups = sortedLobbyGroups.filter { it.getPermission() == null || player.hasPermission(it.getPermission()) }
        val availableServices = groups.map { group -> group.getAllServices().filter { it.isJoinable() }.filter { !it.isFull() } }.flatten()
        val serviceToConnectTo = availableServices.firstOrNull { !filterServices.contains(it.getName()) }
        if (serviceToConnectTo == null) println("WARNING: Lobby server was null.")
        return serviceToConnectTo?.let { ProxyServer.getInstance().getServerInfo(it.getName()) }
    }


}