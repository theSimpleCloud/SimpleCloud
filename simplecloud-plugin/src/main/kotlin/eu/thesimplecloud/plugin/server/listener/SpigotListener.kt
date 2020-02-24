package eu.thesimplecloud.plugin.server.listener

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class SpigotListener : Listener {


    @EventHandler(priority = EventPriority.HIGHEST)
    fun on(event: PlayerQuitEvent) {
        val playerManager = CloudAPI.instance.getCloudPlayerManager()
        val cloudPlayer = playerManager.getCachedCloudPlayer(event.player.uniqueId)

        if (cloudPlayer != null) {
            playerManager.removeCloudPlayer(cloudPlayer)
        }
    }

}