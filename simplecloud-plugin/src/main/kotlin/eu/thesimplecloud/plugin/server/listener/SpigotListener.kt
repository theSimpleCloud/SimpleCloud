package eu.thesimplecloud.plugin.server.listener

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class SpigotListener : Listener {

    private val UNKNOWN_ADRESS = "§cYou are connected from an unknown address!"
    private val NOT_REGISTERED = "§cYou are not registered on the network!"

    @EventHandler
    fun on(event: PlayerLoginEvent) {
        val player = event.player

        val hostAddress = event.realAddress.hostAddress
        //if (hostAddress != "127.0.0.1" && !CloudAPI.instance.getWrapperManager().getAllCachedObjects().any { it.obj.getHost() == hostAddress }) {
        if (!playerIsOnProxy(player.uniqueId, hostAddress)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, UNKNOWN_ADRESS)
            return
        }

        if (CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(player.uniqueId) == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, NOT_REGISTERED)
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun on(event: PlayerQuitEvent) {
        onPlayerDisconnected(event.player)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun on(event: PlayerKickEvent) {
        onPlayerDisconnected(event.player)
    }

    private fun onPlayerDisconnected(player: Player) {
        val playerManager = CloudAPI.instance.getCloudPlayerManager()
        val cloudPlayer = playerManager.getCachedCloudPlayer(player.uniqueId)

        if (cloudPlayer != null) {
            playerManager.removeCloudPlayer(cloudPlayer)
        }
    }

    private fun playerIsOnProxy(uuid: UUID, connectedAddress: String): Boolean {
        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCloudPlayer(uuid).getBlockingOrNull()?: return false

        return cloudPlayer.getPlayerConnection().getAddress().getHostname() == connectedAddress
    }

}