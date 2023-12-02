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

package eu.simplecloud.extension.listener

import eu.thesimplecloud.plugin.server.AbstractServerListener
import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent
import java.net.InetSocketAddress

object MinestomListener : AbstractServerListener() {

    fun register(eventNode: EventNode<Event>) {
        loginListener(eventNode)
        chatListener(eventNode)
        joinListener(eventNode)
        disconnectListener(eventNode)
    }

    private fun loginListener(eventNode: EventNode<Event>) {
        eventNode.addListener(AsyncPlayerPreLoginEvent::class.java) {
            val player = it.player
            val remoteAddress = player.playerConnection.remoteAddress as? InetSocketAddress ?: return@addListener
            val hostAddress = remoteAddress.address.hostAddress

            checkAddress(player.uuid, hostAddress, true) { message ->
                player.kick(message)
            }
        }
    }

//    @EventHandler(priority = EventPriority.LOWEST)
    private fun chatListener(eventNode: EventNode<Event>) {
        eventNode.addListener(PlayerChatEvent::class.java) {
            callChatEvent(it.player.uuid, it.message)
        }
    }

    private fun joinListener(eventNode: EventNode<Event>) {
        eventNode.addListener(PlayerLoginEvent::class.java) {
            updateCurrentOnlineCountTo(MinecraftServer.getConnectionManager().onlinePlayers.size)
        }
    }

//    @EventHandler(priority = EventPriority.HIGHEST)
    private fun disconnectListener(eventNode: EventNode<Event>) {
        eventNode.addListener(PlayerDisconnectEvent::class.java) {
            handleDisconnect(it.player.uuid)
        }
    }

}