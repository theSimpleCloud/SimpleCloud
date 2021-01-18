/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
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

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.plugin.server.ServerEventHandler
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class SpigotListener : Listener {

    private val UNKNOWN_ADRESS = "§cYou are connected from an unknown address!"
    private val NOT_REGISTERED = "§cYou are not registered on the network!"

    @EventHandler
    fun on(event: PlayerLoginEvent) {
        val player = event.player

        val hostAddress = event.realAddress.hostAddress
        if (hostAddress != "127.0.0.1" && !CloudAPI.instance.getWrapperManager().getAllCachedObjects().any { it.getHost() == hostAddress }) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, UNKNOWN_ADRESS)
            return
        }

        if (CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(player.uniqueId) == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, NOT_REGISTERED)
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        ServerEventHandler.updateCurrentOnlineCountTo(Bukkit.getOnlinePlayers().size)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun on(event: PlayerQuitEvent) {
        ServerEventHandler.onPlayerDisconnected(event.player.uniqueId, Bukkit.getOnlinePlayers().size - 1)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun on(event: PlayerKickEvent) {
        ServerEventHandler.onPlayerDisconnected(event.player.uniqueId, Bukkit.getOnlinePlayers().size - 1)
    }
    
}