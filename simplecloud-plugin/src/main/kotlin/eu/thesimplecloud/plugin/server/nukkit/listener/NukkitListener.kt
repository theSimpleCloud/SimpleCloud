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

package eu.thesimplecloud.plugin.server.nukkit.listener

import cn.nukkit.Server
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerJoinEvent
import cn.nukkit.event.player.PlayerKickEvent
import cn.nukkit.event.player.PlayerLoginEvent
import cn.nukkit.event.player.PlayerQuitEvent
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.plugin.server.ServerEventHandler

class NukkitListener : Listener {

    private val UNKNOWN_ADRESS = "§cYou are connected from an unknown address!"
    private val NOT_REGISTERED = "§cYou are not registered on the network!"

    @EventHandler
    fun on(event: PlayerLoginEvent) {
        val player = event.player

        val hostAddress = event.player.address
        if (hostAddress != "127.0.0.1" && !CloudAPI.instance.getWrapperManager().getAllCachedObjects().any { it.getHost() == hostAddress }) {
            event.kickMessage = UNKNOWN_ADRESS
            event.isCancelled = true
            return
        }

        if (CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(player.uniqueId) == null) {
            event.kickMessage = NOT_REGISTERED
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        ServerEventHandler.updateCurrentOnlineCountTo(Server.getInstance().onlinePlayers.size)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun on(event: PlayerQuitEvent) {
        ServerEventHandler.onPlayerDisconnected(event.player.uniqueId, Server.getInstance().onlinePlayers.size - 1)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun on(event: PlayerKickEvent) {
        ServerEventHandler.onPlayerDisconnected(event.player.uniqueId, Server.getInstance().onlinePlayers.size - 1)
    }
    
}