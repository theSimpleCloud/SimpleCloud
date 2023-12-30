package eu.thesimplecloud.plugin.proxy

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudLobbyGroup

/**
 * Date: 30.12.23
 * Time: 21:38
 * @author Frederick Baier
 *
 */
class LobbyServerFinder {

    fun findLobbyServer(filterServices: List<String> = emptyList(), player: PlayerPermission): ICloudService? {
        val lobbyGroups = CloudAPI.instance.getCloudServiceGroupManager().getLobbyGroups()

        val sortedLobbyGroups = lobbyGroups.sortedWith(compareByDescending<ICloudLobbyGroup> { it.getPriority() }
            .thenBy { it.getOnlinePlayerCount() })
            .filter {
                !it.isInMaintenance() || player.hasPermission("cloud.maintenance.join")
            }

        val groups = sortedLobbyGroups.filter {
            it.getPermission() == null || player.hasPermission(it.getPermission()!!)
        }

        val availableServices = groups.map { group ->
            group.getAllServices()
                .filter { it.isOnline() }
                .filter { !it.isFull() }
        }.flatten()

        val serviceToConnectTo = availableServices.firstOrNull { !filterServices.contains(it.getName()) }

        if (serviceToConnectTo == null) println("WARNING: Lobby server was null.")

        return serviceToConnectTo
    }

    fun interface PlayerPermission {

        fun hasPermission(permission: String): Boolean

    }

}