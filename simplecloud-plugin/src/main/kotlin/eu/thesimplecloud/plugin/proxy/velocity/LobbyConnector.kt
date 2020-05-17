package eu.thesimplecloud.plugin.proxy.velocity

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import eu.thesimplecloud.api.CloudAPI

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.05.2020
 * Time: 22:34
 */
class LobbyConnector {

    fun getLobbyServer(player: Player, filterServices: List<String> = emptyList()): RegisteredServer? {
        val lobbyGroups = CloudAPI.instance.getCloudServiceGroupManager().getLobbyGroups()
        val sortedLobbyGroups = lobbyGroups.sortedBy { it.getPriority() }.filter { !it.isInMaintenance() || player.hasPermission("cloud.maintenance.join") }
        val groups = sortedLobbyGroups.filter { it.getPermission() == null || player.hasPermission(it.getPermission()) }
        val availableServices = groups.map { group -> group.getAllServices().filter { it.isOnline() }.filter { !it.isFull() } }.flatten()
        val serviceToConnectTo = availableServices.firstOrNull { !filterServices.contains(it.getName()) }
        if (serviceToConnectTo == null) println("WARNING: Lobby server was null.")
        return serviceToConnectTo?.let { CloudVelocityPlugin.instance.proxyServer.getServer(it.getName()).orElse(null) }
    }

}