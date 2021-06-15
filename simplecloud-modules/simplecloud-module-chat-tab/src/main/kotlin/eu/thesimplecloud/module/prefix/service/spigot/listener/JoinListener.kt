package eu.thesimplecloud.module.prefix.service.spigot.listener

import eu.thesimplecloud.module.prefix.service.tablist.TablistHelper
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 19.12.2020
 * Time: 15:36
 */
class JoinListener : Listener {

    @EventHandler
    fun handleJoin(event: PlayerJoinEvent) {
        val player = event.player

        TablistHelper.updateScoreboardForAllPlayers()
    }

}