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

package eu.thesimplecloud.plugin.proxy.velocity.listener

import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.ResultedEvent
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.event.player.KickedFromServerEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.connection.DefaultPlayerAddress
import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import eu.thesimplecloud.plugin.proxy.CancelType
import eu.thesimplecloud.plugin.proxy.ProxyEventHandler
import eu.thesimplecloud.plugin.proxy.velocity.CloudVelocityPlugin
import eu.thesimplecloud.plugin.proxy.velocity.text.CloudTextBuilder
import net.kyori.adventure.text.TextComponent
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.05.2020
 * Time: 21:32
 */
class VelocityListener(val plugin: CloudVelocityPlugin) {

    @Subscribe(order = PostOrder.FIRST)
    fun handle(event: LoginEvent) {
        val player = event.player

        val playerAddress = DefaultPlayerAddress(player.remoteAddress.hostString, player.remoteAddress.port)
        val playerConnection = DefaultPlayerConnection(
            playerAddress,
            player.username,
            player.uniqueId,
            plugin.proxyServer.configuration.isOnlineMode,
            player.protocolVersion.protocol
        )


        ProxyEventHandler.handleLogin(playerConnection) {
            event.result = ResultedEvent.ComponentResult.denied(CloudTextBuilder().build(CloudText(it)))
        }
    }

    @Subscribe
    fun handle(event: PostLoginEvent) {
        val player = event.player

        ProxyEventHandler.handlePostLogin(player.uniqueId, player.username)

        if (plugin.lobbyConnector.getLobbyServer(player) == null) {
            event.player.disconnect(CloudTextBuilder().build(CloudText(getNoFallbackServerFoundMessage())))
        }
    }


    @Subscribe
    fun handle(event: DisconnectEvent) {
        val player = event.player

        ProxyEventHandler.handleDisconnect(player.uniqueId, player.username)
    }

    @Subscribe
    fun handle(event: ServerPreConnectEvent) {
        if (!event.result.isAllowed) return
        val player = event.player

        val target = if (event.originalServer.serverInfo.name == "fallback")
            plugin.lobbyConnector.getLobbyServer(player)
        else
            event.originalServer
        if (target == null) {
            event.player.disconnect(CloudTextBuilder().build(CloudText(getNoFallbackServerFoundMessage())))
            return
        }
        val serverNameTo = target.serverInfo.name
        event.result =
            ServerPreConnectEvent.ServerResult.allowed(plugin.proxyServer.getServer(serverNameTo).orElse(null))

        var serverNameFrom: String? = null

        val currentServer = player.currentServer
        if (currentServer.isPresent) {
            serverNameFrom = currentServer.get().serverInfo.name
        }

        ProxyEventHandler.handleServerPreConnect(
            player.uniqueId,
            serverNameFrom,
            serverNameTo
        ) { message, cancelMessageType ->
            if (cancelMessageType == CancelType.MESSAGE) {
                player.sendMessage(CloudTextBuilder().build(CloudText(message)))
            } else {
                player.disconnect(CloudTextBuilder().build(CloudText(message)))
            }
            event.result = ServerPreConnectEvent.ServerResult.denied()
        }
    }

    @Subscribe
    fun handle(event: ServerConnectedEvent) {
        val player = event.player
        ProxyEventHandler.handleServerConnect(player.uniqueId, event.server.serverInfo.name) {
            player.disconnect(CloudTextBuilder().build(CloudText("§cService does not exist.")))
        }
    }

    @Subscribe
    fun handle(event: KickedFromServerEvent) {
        val kickReasonString: String
        var kickReasonComponent = event.serverKickReason
        if (!kickReasonComponent.isPresent) {
            kickReasonString = ""
            kickReasonComponent = Optional.of(CloudTextBuilder().build(CloudText("")))
        } else {
            val component = kickReasonComponent.get()
            kickReasonString = if (component is TextComponent) {
                component.content()
            } else {
                "§cYou were kicked from the server"
            }
        }

        val player = event.player
        val kickedServerName = event.server.serverInfo.name
        ProxyEventHandler.handleServerKick(
            player.getCloudPlayer(),
            kickReasonString,
            kickedServerName
        ) { message, cancelMessageType ->
            if (cancelMessageType == CancelType.MESSAGE) {
                player.sendMessage(CloudTextBuilder().build(CloudText(message)))
            } else {
                event.result = KickedFromServerEvent.DisconnectPlayer.create(kickReasonComponent.get())
            }
        }

        val fallback = plugin.lobbyConnector.getLobbyServer(player, listOf(kickedServerName))
        if (fallback == null) {
            event.result = KickedFromServerEvent.DisconnectPlayer.create(
                CloudTextBuilder().build(
                    CloudText(getNoFallbackServerFoundMessage())
                )
            )
            return
        }

        event.result = KickedFromServerEvent.RedirectPlayer.create(fallback)
    }

    private fun getNoFallbackServerFoundMessage(): String {
        return CloudAPI.instance.getLanguageManager().getMessage("ingame.no-fallback-server-found")
    }

}