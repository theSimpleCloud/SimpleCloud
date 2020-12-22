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

package eu.thesimplecloud.plugin.proxy.bungee.listener

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.connection.DefaultPlayerAddress
import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import eu.thesimplecloud.plugin.proxy.CancelType
import eu.thesimplecloud.plugin.proxy.ProxyEventHandler
import eu.thesimplecloud.plugin.proxy.bungee.CloudBungeePlugin
import eu.thesimplecloud.plugin.proxy.bungee.text.CloudTextBuilder
import net.md_5.bungee.api.event.*
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority

class BungeeListener : Listener {

    @EventHandler(priority = EventPriority.LOW)
    fun on(event: LoginEvent) {
        if (event.isCancelled) return
        val connection = event.connection

        val playerAddress = DefaultPlayerAddress(connection.address.hostString, connection.address.port)
        val playerConnection = DefaultPlayerConnection(playerAddress, connection.name, connection.uniqueId, connection.isOnlineMode, connection.version)


        ProxyEventHandler.handleLogin(playerConnection) {
            event.isCancelled = true
            event.setCancelReason(CloudTextBuilder().build(CloudText(it)))
        }

    }

    @EventHandler
    fun on(event: PostLoginEvent) {
        val proxiedPlayer = event.player
        proxiedPlayer.reconnectServer = null

        ProxyEventHandler.handlePostLogin(proxiedPlayer.uniqueId, proxiedPlayer.name)

        if (CloudBungeePlugin.instance.lobbyConnector.getLobbyServer(proxiedPlayer) == null) {
            event.player.disconnect(CloudTextBuilder().build(CloudText(getNoFallbackServerFoundMessage())))
            return
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun on(event: PlayerDisconnectEvent) {
        val proxiedPlayer = event.player

        ProxyEventHandler.handleDisconnect(proxiedPlayer.uniqueId, proxiedPlayer.name)
    }

    @EventHandler
    fun on(event: ServerConnectEvent) {
        if (event.isCancelled) return
        val proxiedPlayer = event.player
        val target = if (event.target.name == "fallback")
            CloudBungeePlugin.instance.lobbyConnector.getLobbyServer(proxiedPlayer)
        else
            event.target
        if (target == null) {
            event.player.disconnect(CloudTextBuilder().build(CloudText(getNoFallbackServerFoundMessage())))
            return
        }
        event.target = target


        val name: String? = proxiedPlayer.server?.info?.name

        ProxyEventHandler.handleServerPreConnect(proxiedPlayer.uniqueId, name, target.name) { message, cancelMessageType ->
            if (cancelMessageType == CancelType.MESSAGE) {
                proxiedPlayer.sendMessage(CloudTextBuilder().build(CloudText(message)))
            } else {
                proxiedPlayer.disconnect(CloudTextBuilder().build(CloudText(message)))
            }
            event.isCancelled = true
        }
    }

    @EventHandler
    fun on(event: ServerConnectedEvent) {
        val proxiedPlayer = event.player
        ProxyEventHandler.handleServerConnect(proxiedPlayer.uniqueId, event.server.info.name) {
            proxiedPlayer.disconnect(CloudTextBuilder().build(CloudText("§cService does not exist.")))
        }
    }

    @EventHandler
    fun on(event: ServerKickEvent) {
        if (event.isCancelled) return
        val kickReasonString: String = if (event.kickReasonComponent.isEmpty()) {
            ""
        } else {
            event.kickReasonComponent[0].toLegacyText()
        }

        val proxiedPlayer = event.player
        val kickedServerName = event.kickedFrom.name
        ProxyEventHandler.handleServerKick(proxiedPlayer.getCloudPlayer(), kickReasonString, kickedServerName) { message, cancelMessageType ->
            if (cancelMessageType == CancelType.MESSAGE) {
                proxiedPlayer.sendMessage(CloudTextBuilder().build(CloudText(message)))
            } else {
                proxiedPlayer.disconnect(CloudTextBuilder().build(CloudText(message)))
            }
        }

        val fallback = CloudBungeePlugin.instance.lobbyConnector.getLobbyServer(proxiedPlayer, listOf(kickedServerName))
        if (fallback == null) {
            proxiedPlayer.disconnect(CloudTextBuilder().build(CloudText(getNoFallbackServerFoundMessage())))
            return
        }

        proxiedPlayer.sendMessage(*event.kickReasonComponent)
        event.cancelServer = fallback
        event.isCancelled = true
    }

    private fun getNoFallbackServerFoundMessage(): String {
        return CloudAPI.instance.getLanguageManager().getMessage("ingame.no-fallback-server-found")
    }
}