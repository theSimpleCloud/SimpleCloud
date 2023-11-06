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

package eu.thesimplecloud.plugin.server.spigot.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.server.ServerCommandEvent

/**
 * Created by IntelliJ IDEA.
 * Date: 24.12.2020
 * Time: 10:50
 * @author Frederick Baier
 */
class ReloadCommandBlocker : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handle(event: PlayerCommandPreprocessEvent) {
        val message = event.message
        val player = event.player
        if (isCommandBlocked("/$message")) {
            if (player.hasPermission("bukkit.command.reload")) {
                event.isCancelled = true
                player.sendMessage("§cCloud-Servers cannot be reloaded")
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun handle(event: ServerCommandEvent) {
        if (isCommandBlocked(event.command)) {
            event.isCancelled = true
        }
    }

    private fun isCommandBlocked(message: String): Boolean {
        return message.equals("rl", true) ||
                message.equals("reload", true) ||
                message.equals("rl confirm", true) ||
                message.equals("reload confirm", true)
    }

}