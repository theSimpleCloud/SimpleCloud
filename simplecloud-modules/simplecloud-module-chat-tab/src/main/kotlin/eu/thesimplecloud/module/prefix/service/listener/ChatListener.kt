package eu.thesimplecloud.module.prefix.service.listener

import eu.thesimplecloud.module.prefix.config.Config
import eu.thesimplecloud.module.prefix.config.TablistInformation
import eu.thesimplecloud.module.prefix.service.tablist.TablistHelper
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 19.12.2020
 * Time: 15:36
 */
class ChatListener : Listener {

    @EventHandler
    fun handleJoin(event: AsyncPlayerChatEvent) {
        val player = event.player

        val tablistInformation = TablistHelper.getTablistInformationByPlayer(player) ?: return
        val format = Config.getConfig().chatFormat
                .replace("%PLAYER%", buildPrompt(tablistInformation))
                .replace("%MESSAGE%", "%2\$s")

        event.format = format
    }

    private fun buildPrompt(information: TablistInformation): String {
        return information.prefix + ChatColor.valueOf(information.color) + "%1\$s" + information.suffix
    }

}