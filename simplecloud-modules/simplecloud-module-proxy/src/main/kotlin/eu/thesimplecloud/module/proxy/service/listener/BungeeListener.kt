package eu.thesimplecloud.module.proxy.service.listener

import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.module.proxy.extensions.mapToLowerCase
import eu.thesimplecloud.module.proxy.service.BungeePluginMain
import eu.thesimplecloud.plugin.proxy.text.CloudTextBuilder
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.ServerPing
import net.md_5.bungee.api.event.ProxyPingEvent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.03.2020
 * Time: 22:14
 */
class BungeeListener(val plugin: BungeePluginMain) : Listener {

    val JOIN_MAINTENANCE_PERMISSION = "cloud.maintenance.join"
    val JOIN_FULL_PERMISSION = "cloud.full.join"

    @EventHandler(priority = EventPriority.HIGHEST)
    fun on(event: ServerConnectEvent) {
        val player = event.player

        if (!plugin.tablistStarted) {
            plugin.startTablist()
        }

        val config = plugin.config
        val proxyConfiguration = plugin.getProxyConfiguration()?: return

        if (plugin.thisService.getServiceGroup().isInMaintenance()) {
            if (!player.hasPermission(JOIN_MAINTENANCE_PERMISSION) &&
                    !proxyConfiguration.whitelist.mapToLowerCase().contains(player.name.toLowerCase())) {
                player.disconnect(CloudTextBuilder().build(CloudText(config.maintenanceKickMessage)))
                event.isCancelled = true
            }

            return
        }


        val maxPlayers = plugin.thisService.getMaxPlayers()

        if (plugin.getOnlinePlayers() >= maxPlayers) {
            if (!player.hasPermission(JOIN_FULL_PERMISSION) &&
                    !proxyConfiguration.whitelist.mapToLowerCase().contains(player.name.toLowerCase())) {
                player.disconnect(CloudTextBuilder().build(CloudText(config.fullProxyKickMessage)))
                event.isCancelled = true
            }
        }

    }

    @EventHandler
    fun on(event: ProxyPingEvent) {
        val response = event.response

        val config = plugin.config

        val proxyConfiguration = plugin.getProxyConfiguration() ?: return
        val motdConfiguration = if (plugin.thisService.getServiceGroup().isInMaintenance())
            proxyConfiguration.maintenanceMotds else proxyConfiguration.motds

        val line1 = motdConfiguration.firstLines.random()
        val line2 = motdConfiguration.secondLines.random()

        response.descriptionComponent = CloudTextBuilder()
                .build(CloudText(plugin.replaceString(line1 + "\n" + line2)))


        val playerInfo = motdConfiguration.playerInfo
        var onlinePlayers = plugin.getOnlinePlayers()

        val versionName = motdConfiguration.versionName
        if (versionName != null && versionName.isNotEmpty()) {
            response.version = ServerPing.Protocol(plugin.replaceString(versionName), -1)
        }

        val maxPlayers = plugin.thisService.getMaxPlayers()

        if (playerInfo != null && playerInfo.isNotEmpty()) {
            val playerInfoString = plugin.replaceString(playerInfo.joinToString("\n"))
            val sample = arrayOf(ServerPing.PlayerInfo(playerInfoString, ""))
            response.players = ServerPing.Players(maxPlayers, onlinePlayers, sample)

            return
        }

        response.players.max = maxPlayers
        response.players.online = onlinePlayers
    }

}