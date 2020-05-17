package eu.thesimplecloud.module.proxy.service.bungee.listener

import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.module.proxy.extensions.mapToLowerCase
import eu.thesimplecloud.module.proxy.service.bungee.BungeePluginMain
import eu.thesimplecloud.plugin.proxy.bungee.text.CloudTextBuilder
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.ServerPing
import net.md_5.bungee.api.event.ProxyPingEvent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.event.ServerSwitchEvent
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

    @EventHandler(priority = EventPriority.HIGHEST)
    fun on(event: ServerConnectEvent) {
        val player = event.player

        if (!plugin.tablistStarted) {
            plugin.startTablist()
        }

        val config = plugin.proxyHandler.configHolder.obj
        val proxyConfiguration = plugin.proxyHandler.getProxyConfiguration()?: return

        if (CloudPlugin.instance.thisService().getServiceGroup().isInMaintenance()) {
            if (!player.hasPermission(plugin.proxyHandler.JOIN_MAINTENANCE_PERMISSION) &&
                    !proxyConfiguration.whitelist.mapToLowerCase().contains(player.name.toLowerCase())) {
                player.disconnect(CloudTextBuilder().build(CloudText(config.maintenanceKickMessage)))
                event.isCancelled = true
            }

            return
        }


        val maxPlayers = CloudPlugin.instance.thisService().getMaxPlayers()

        if (plugin.proxyHandler.getOnlinePlayers() >= maxPlayers) {
            if (!player.hasPermission(plugin.proxyHandler.JOIN_FULL_PERMISSION) &&
                    !proxyConfiguration.whitelist.mapToLowerCase().contains(player.name.toLowerCase())) {
                player.disconnect(CloudTextBuilder().build(CloudText(config.fullProxyKickMessage)))
                event.isCancelled = true
            }
        }

        val tablistConfiguration = plugin.proxyHandler.getTablistConfiguration()?: return

        val headerAndFooter = plugin.getCurrentHeaderAndFooter(tablistConfiguration)
        plugin.sendHeaderAndFooter(player, headerAndFooter.first, headerAndFooter.second)
    }

    @EventHandler
    fun on(event: ServerSwitchEvent) {
        val player = event.player

        val tablistConfiguration = plugin.proxyHandler.getTablistConfiguration()?: return

        val headerAndFooter = plugin.getCurrentHeaderAndFooter(tablistConfiguration)
        plugin.sendHeaderAndFooter(player, headerAndFooter.first, headerAndFooter.second)
    }

    @EventHandler
    fun on(event: ProxyPingEvent) {
        val response = event.response

        val proxyConfiguration = plugin.proxyHandler.getProxyConfiguration() ?: return
        val motdConfiguration = if (CloudPlugin.instance.thisService().getServiceGroup().isInMaintenance())
            proxyConfiguration.maintenanceMotds else proxyConfiguration.motds

        val line1 = motdConfiguration.firstLines.random()
        val line2 = motdConfiguration.secondLines.random()

        response.descriptionComponent = CloudTextBuilder()
                .build(CloudText(plugin.proxyHandler.replaceString(line1 + "\n" + line2)))


        val playerInfo = motdConfiguration.playerInfo
        var onlinePlayers = plugin.proxyHandler.getOnlinePlayers()

        val versionName = motdConfiguration.versionName
        if (versionName != null && versionName.isNotEmpty()) {
            response.version = ServerPing.Protocol(plugin.proxyHandler.replaceString(versionName), -1)
        }

        val maxPlayers = CloudPlugin.instance.thisService().getMaxPlayers()

        if (playerInfo != null && playerInfo.isNotEmpty()) {
            val playerInfoString = plugin.proxyHandler.replaceString(playerInfo.joinToString("\n"))
            val sample = arrayOf(ServerPing.PlayerInfo(playerInfoString, ""))
            response.players = ServerPing.Players(maxPlayers, onlinePlayers, sample)

            return
        }

        response.players.max = maxPlayers
        response.players.online = onlinePlayers
    }

}