package eu.thesimplecloud.plugin.server.listener

import eu.thesimplecloud.api.CloudAPI
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class SpigotListener : Listener {

    private val UNKNOWN_ADRESS = "§cYou are connected from an unknown address!"
    private val NOT_REGISTERED = "§cYou are not registered on the network!"

    @EventHandler
    fun on(event: PlayerLoginEvent) {
        val hostAddress = event.realAddress.hostAddress
        if (hostAddress != "127.0.0.1" && !CloudAPI.instance.getWrapperManager().getAllWrappers().any { it.getHost() == hostAddress }) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, UNKNOWN_ADRESS)
            return
        }

        if (CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(event.player.uniqueId) == null) {
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

}