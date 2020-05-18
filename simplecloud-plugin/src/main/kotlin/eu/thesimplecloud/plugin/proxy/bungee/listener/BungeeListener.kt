package eu.thesimplecloud.plugin.proxy.bungee.listener

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.CloudPlayer
import eu.thesimplecloud.api.player.connection.DefaultPlayerAddress
import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import eu.thesimplecloud.plugin.network.packets.PacketOutGetTabSuggestions
import eu.thesimplecloud.plugin.proxy.CancelMessageType
import eu.thesimplecloud.plugin.proxy.ProxyEventHandler
import eu.thesimplecloud.plugin.proxy.bungee.CloudBungeePlugin
import eu.thesimplecloud.plugin.proxy.bungee.text.CloudTextBuilder
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.connection.Server
import net.md_5.bungee.api.event.*
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority

class BungeeListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun on(event: LoginEvent) {
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
            event.player.disconnect(CloudTextBuilder().build(CloudText("§cNo fallback server found")))
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
        val proxiedPlayer = event.player
        val target = if (event.target.name == "fallback")
            CloudBungeePlugin.instance.lobbyConnector.getLobbyServer(proxiedPlayer)
        else
            event.target
        if (target == null) {
            event.player.disconnect(CloudTextBuilder().build(CloudText("§cNo fallback server found")))
            return
        }
        event.target = target


        val name: String?

        val server = proxiedPlayer.server
        if (server != null) {
            name = server.info.name
        } else {
            name = null
        }

        ProxyEventHandler.handleServerPreConnect(proxiedPlayer.uniqueId, name, target.name) { message, cancelMessageType ->
            if (cancelMessageType == CancelMessageType.MESSAGE) {
                proxiedPlayer.sendMessage(CloudTextBuilder().build(CloudText(message)))
            } else {
                proxiedPlayer.disconnect(CloudTextBuilder().build(CloudText(message)))
            }
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
        val kickReasonString: String
        if (event.kickReasonComponent.isEmpty()) {
            kickReasonString = ""
        } else {
            kickReasonString = event.kickReasonComponent[0].toLegacyText()
        }

        val proxiedPlayer = event.player
        val kickedServerName = event.kickedFrom.name
        ProxyEventHandler.handleServerKick(kickReasonString, kickedServerName) { message, cancelMessageType ->
            if (cancelMessageType == CancelMessageType.MESSAGE) {
                proxiedPlayer.sendMessage(CloudTextBuilder().build(CloudText(message)))
            } else {
                proxiedPlayer.disconnect(CloudTextBuilder().build(CloudText(message)))
            }
        }

        val fallback = CloudBungeePlugin.instance.lobbyConnector.getLobbyServer(proxiedPlayer, listOf(kickedServerName))
        if (fallback == null) {
            proxiedPlayer.disconnect(CloudTextBuilder().build(CloudText("§cNo fallback server found")))
            return
        }

        proxiedPlayer.sendMessage(*event.kickReasonComponent)
        event.cancelServer = fallback
        event.isCancelled = true
    }

}