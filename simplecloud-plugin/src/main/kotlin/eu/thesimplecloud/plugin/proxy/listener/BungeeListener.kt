package eu.thesimplecloud.plugin.proxy.listener

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.player.CloudPlayerDisconnectEvent
import eu.thesimplecloud.api.event.player.CloudPlayerLoginEvent
import eu.thesimplecloud.api.network.packets.player.PacketIORemoveCloudPlayer
import eu.thesimplecloud.api.network.packets.player.PacketIOUpdateCloudPlayer
import eu.thesimplecloud.api.player.CloudPlayer
import eu.thesimplecloud.api.player.connection.DefaultPlayerAddress
import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import eu.thesimplecloud.plugin.network.packets.PacketOutCreateCloudPlayer
import eu.thesimplecloud.plugin.network.packets.PacketOutGetTabSuggestions
import eu.thesimplecloud.plugin.network.packets.PacketOutPlayerConnectToServer
import eu.thesimplecloud.plugin.network.packets.PacketOutPlayerLoginRequest
import eu.thesimplecloud.plugin.proxy.CloudProxyPlugin
import eu.thesimplecloud.plugin.proxy.text.CloudTextBuilder
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.*
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority

class BungeeListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun on(event: LoginEvent) {
        val connection = event.connection

        if (!CloudPlugin.instance.communicationClient.isOpen()) {
            event.isCancelled = true
            event.setCancelReason(CloudTextBuilder().build(CloudText("§cProxy still starting.")))
            return
        }
        val playerPromise = CloudAPI.instance.getCloudPlayerManager().getCloudPlayer(event.connection.uniqueId).awaitUninterruptibly()
        if (playerPromise.isSuccess) {
            event.isCancelled = true
            event.setCancelReason(CloudTextBuilder().build(CloudText("§cYou are already registered on the network")))
            return
        }
        val playerAddress = DefaultPlayerAddress(connection.address.hostString, connection.address.port)
        val playerConnection = DefaultPlayerConnection(playerAddress, connection.name, connection.uniqueId, connection.isOnlineMode, connection.version)

        //send login request
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketOutCreateCloudPlayer(playerConnection, CloudPlugin.instance.thisServiceName)).awaitUninterruptibly()
        val loginRequestPromise = CloudPlugin.instance.communicationClient.sendUnitQuery(PacketOutPlayerLoginRequest(connection.uniqueId)).awaitUninterruptibly()
        loginRequestPromise.addFailureListener {
            event.isCancelled = true
            event.setCancelReason(CloudTextBuilder().build(CloudText("§cLogin failed: " + it.message)))
        }
    }

    @EventHandler
    fun on(event: PostLoginEvent) {
        val proxiedPlayer = event.player
        proxiedPlayer.reconnectServer = null


        //update service
        val thisService = CloudPlugin.instance.thisService()
        thisService.setOnlineCount(thisService.getOnlineCount() + 1)
        thisService.update()

        //call event
        CloudAPI.instance.getEventManager().call(CloudPlayerLoginEvent(proxiedPlayer.uniqueId, proxiedPlayer.name))
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun on(event: PlayerDisconnectEvent) {
        val proxiedPlayer = event.player
        //call event
        CloudAPI.instance.getEventManager().call(CloudPlayerDisconnectEvent(proxiedPlayer.uniqueId, proxiedPlayer.name))

        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(proxiedPlayer.uniqueId)
        cloudPlayer?.let {
            cloudPlayer as CloudPlayer
            cloudPlayer.setOffline()
            cloudPlayer.let { CloudAPI.instance.getCloudPlayerManager().removeCloudPlayer(it) }
            //send update that the player is now offline
            CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOUpdateCloudPlayer(cloudPlayer)).addCompleteListener {
                CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIORemoveCloudPlayer(cloudPlayer.getUniqueId()))
            }
        }

        //update service
        val thisService = CloudPlugin.instance.thisService()
        thisService.setOnlineCount(thisService.getOnlineCount() - 1)
        thisService.update()
    }

    @EventHandler
    fun on(event: ServerConnectEvent) {
        val proxiedPlayer = event.player
        val target = event.target
        val cloudService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(target.name)
        if (cloudService == null){
            proxiedPlayer.sendMessage(CloudTextBuilder().build(CloudText("§cServer is not registered in the cloud.")))
            event.isCancelled = true
            return
        }
        if (cloudService.getState() == ServiceState.STARTING){
            proxiedPlayer.sendMessage(CloudTextBuilder().build(CloudText("§cServer is still starting.")))
            event.isCancelled = true
            return
        }
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketOutPlayerConnectToServer(proxiedPlayer.uniqueId, target.name))
                .awaitUninterruptibly()
                .addFailureListener {
                    proxiedPlayer.sendMessage(CloudTextBuilder().build(CloudText("§cCan't connect to server: " + it.message)))
                    event.isCancelled = true
                }
    }

    @EventHandler
    fun on(event: ServerConnectedEvent) {
        val proxiedPlayer = event.player
        val service = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(event.server.info.name)
        if (service == null){
            proxiedPlayer.disconnect(CloudTextBuilder().build(CloudText("§cService does not exist.")))
            return
        }
        service.setOnlineCount(service.getOnlineCount() + 1)
        service.update()

        //update player
        //use cloned player to compare connected server with old connected server.
        val cloudPlayer = proxiedPlayer.getCloudPlayer()
        val clonedPlayer = cloudPlayer.clone() as CloudPlayer
        clonedPlayer.setConnectedServerName(service.getName())
        clonedPlayer.update()
    }

    @EventHandler
    fun on(event: ServerDisconnectEvent) {
        val service = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(event.target.name) ?: return
        service.setOnlineCount(service.getOnlineCount() - 1)
        service.update()
    }

    @EventHandler
    fun on(event: ServerKickEvent) {
        if (event.kickReasonComponent.isNotEmpty() && event.kickReasonComponent[0].toLegacyText().contains("Outdated server") || event.kickReasonComponent[0].toLegacyText().contains("Outdated client")) {
            val cloudService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(event.kickedFrom.name)
            if (cloudService == null || cloudService.isLobby()) {
                event.player.disconnect(CloudTextBuilder().build(CloudText("§cYou are using an unsupported version.")))
                return
            }
        }
        val fallback = CloudProxyPlugin.instance.lobbyConnector.getLobbyServer(event.player, listOf(event.kickedFrom.name))
        if (fallback == null) {
           event.player.disconnect(CloudTextBuilder().build(CloudText("§cNo fallback server found")))
            return
        }
        event.player.sendMessage(*event.kickReasonComponent)
        event.cancelServer = fallback
        event.isCancelled = true
    }

    @EventHandler
    fun on(event: TabCompleteEvent) {
        val player = event.sender as ProxiedPlayer

        val commandString = event.cursor.replace("/", "")
        if (commandString.isEmpty()) return

        val suggestions = CloudPlugin.instance.communicationClient.sendQuery<Array<String>>(PacketOutGetTabSuggestions(player.uniqueId, commandString)).awaitUninterruptibly().getNow()
        if (suggestions.isEmpty()) {
            return
        }
        event.suggestions.addAll(suggestions)




    }

}