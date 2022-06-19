/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.module.prefix.service.spigot.listener

import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.prefix.config.Config
import eu.thesimplecloud.module.prefix.config.TablistInformation
import eu.thesimplecloud.module.prefix.service.tablist.TablistHelper
import org.bukkit.ChatColor
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
        event.message = event.message.replace("%", "%%")

        val permissionPlayer =
            PermissionPool.instance.getPermissionPlayerManager().getCachedPermissionPlayer(player.uniqueId) ?: return
        val canWriteColored = event.player.hasPermission("cloud.module.chat.color")
        val tablistInformation = TablistHelper.getTablistInformationByUUID(player.uniqueId) ?: return
        val format = ChatColor.translateAlternateColorCodes('&', Config.getConfig().chatFormat)
            .replace("%PLAYER%", buildPrompt(tablistInformation))
            .replace("%NAME%", event.player.name)
            .replace("%PRIORITY%", tablistInformation.priority.toString())
            .replace("%PREFIX%", ChatColor.translateAlternateColorCodes('&', tablistInformation.prefix))
            .replace("%SUFFIX%", ChatColor.translateAlternateColorCodes('&', tablistInformation.suffix))
            .replace("%COLOR%", tablistInformation.color)
            .replace("%COLOR_CODE%", ChatColor.valueOf(tablistInformation.color).toString())
            .replace(
                "%MESSAGE%",
                if (canWriteColored) ChatColor.translateAlternateColorCodes('&', event.message) else "%2\$s"
            )
            .replace("%GROUP%", permissionPlayer.getHighestPermissionGroup().getName())

        event.format = format
    }

    private fun buildPrompt(information: TablistInformation): String {
        return information.prefix + ChatColor.valueOf(information.color).toString() + "%1\$s"
    }

}