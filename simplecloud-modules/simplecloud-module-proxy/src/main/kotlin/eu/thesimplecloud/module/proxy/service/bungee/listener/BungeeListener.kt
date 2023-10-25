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

package eu.thesimplecloud.module.proxy.service.bungee.listener

import eu.thesimplecloud.module.proxy.extensions.mapToLowerCase
import eu.thesimplecloud.module.proxy.service.ProxyHandler
import eu.thesimplecloud.module.proxy.service.bungee.BungeePluginMain
import eu.thesimplecloud.plugin.proxy.bungee.toBaseComponent
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.ServerPing
import net.md_5.bungee.api.event.ProxyPingEvent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.event.ServerSwitchEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.03.2020
 * Time: 22:14
 */
class BungeeListener(private val plugin: BungeePluginMain) : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun on(event: ServerConnectEvent) {
        val player = event.player

        val config = ProxyHandler.configHolder.getValue()
        val proxyConfiguration = ProxyHandler.getProxyConfiguration() ?: return

        if (CloudPlugin.instance.thisService().getServiceGroup().isInMaintenance()) {
            if (!player.hasPermission(ProxyHandler.JOIN_MAINTENANCE_PERMISSION) &&
                !proxyConfiguration.whitelist.mapToLowerCase().contains(player.name.lowercase())
            ) {
                player.disconnect(
                    ProxyHandler.getHexColorComponent(ProxyHandler.replaceString(config.maintenanceKickMessage))
                        .toBaseComponent()
                )
                event.isCancelled = true
                return
            }
        }


        val maxPlayers = CloudPlugin.instance.thisService().getServiceGroup().getMaxPlayers()

        if (ProxyHandler.getOnlinePlayers() > maxPlayers) {
            if (!player.hasPermission(ProxyHandler.JOIN_FULL_PERMISSION) &&
                !proxyConfiguration.whitelist.mapToLowerCase().contains(player.name.lowercase())
            ) {
                player.disconnect(
                    ProxyHandler.getHexColorComponent(ProxyHandler.replaceString(config.fullProxyKickMessage))
                        .toBaseComponent()
                )
                event.isCancelled = true
            }
        }

    }

    @EventHandler
    fun on(event: ServerConnectedEvent) {
        val player = event.player
        val tablistConfiguration = ProxyHandler.getCurrentTablistConfiguration() ?: return
        plugin.sendHeaderAndFooter(player, tablistConfiguration)
    }

    @EventHandler
    fun on(event: ServerSwitchEvent) {
        val player = event.player

        val tablistConfiguration = ProxyHandler.getCurrentTablistConfiguration() ?: return
        plugin.sendHeaderAndFooter(player, tablistConfiguration)
    }

    @EventHandler
    fun on(event: ProxyPingEvent) {
        val response = event.response

        val proxyConfiguration = ProxyHandler.getProxyConfiguration() ?: return
        val motdConfiguration = if (CloudPlugin.instance.thisService().getServiceGroup().isInMaintenance())
            proxyConfiguration.maintenanceMotds else proxyConfiguration.motds

        val line1 = motdConfiguration.firstLines.random()
        val line2 = motdConfiguration.secondLines.random()

        val motdComponent = ProxyHandler.getHexColorComponent(ProxyHandler.replaceString("$line1\n$line2"))
        response.descriptionComponent = motdComponent.toBaseComponent()

        val playerInfo = motdConfiguration.playerInfo
        val onlinePlayers = ProxyHandler.getOnlinePlayers()

        val versionName = motdConfiguration.versionName
        if (versionName != null && versionName.isNotEmpty()) {
            val versionColorComponent = ProxyHandler.getHexColorComponent(ProxyHandler.replaceString(versionName))
            response.version = ServerPing.Protocol(
                LegacyComponentSerializer.legacy('§').serialize(versionColorComponent),
                -1
            )
        }

        val maxPlayers = CloudPlugin.instance.thisService().getServiceGroup().getMaxPlayers()

        if (playerInfo != null && playerInfo.isNotEmpty()) {
            val sample = mutableListOf<ServerPing.PlayerInfo>()

            playerInfo.forEach {
                val playerInfoString = ProxyHandler.replaceString(it)
                val playerInfoComponent = ProxyHandler.getHexColorComponent(playerInfoString)

                sample.add(
                    ServerPing.PlayerInfo(
                        LegacyComponentSerializer.legacy('§').serialize(playerInfoComponent),
                        UUID.randomUUID()
                    )
                )
            }

            response.players = ServerPing.Players(maxPlayers, onlinePlayers, sample.toTypedArray())

            return
        }

        response.players.max = maxPlayers
        response.players.online = onlinePlayers
    }

}