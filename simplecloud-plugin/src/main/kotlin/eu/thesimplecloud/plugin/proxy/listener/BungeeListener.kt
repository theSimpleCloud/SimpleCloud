package eu.thesimplecloud.plugin.proxy.listener

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.network.packets.player.PacketIORemoveCloudPlayer
import eu.thesimplecloud.api.network.packets.player.PacketIOUpdateCloudPlayer
import eu.thesimplecloud.api.network.packets.service.PacketIOUpdateCloudService
import eu.thesimplecloud.api.parser.string.typeparser.CloudLobbyGroupParser
import eu.thesimplecloud.api.player.CloudPlayer
import eu.thesimplecloud.api.player.connection.DefaultPlayerAddress
import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import eu.thesimplecloud.plugin.network.packets.PacketOutCreateCloudPlayer
import eu.thesimplecloud.plugin.network.packets.PacketOutPlayerConnectToServer
import eu.thesimplecloud.plugin.network.packets.PacketOutPlayerLoginRequest
import eu.thesimplecloud.plugin.proxy.CloudProxyPlugin
import eu.thesimplecloud.plugin.proxy.text.CloudTextBuilder
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.*
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority

class BungeeListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun on(event: LoginEvent) {
        val connection = event.connection
        val promise = CloudAPI.instance.getCloudPlayerManager().getCloudPlayer(event.connection.uniqueId).awaitUninterruptibly()
        if (promise.isSuccess) {
            event.setCancelReason(CloudTextBuilder().build(CloudText("§cYou are already registered in the network")))
            return
        }
        val playerAddress = DefaultPlayerAddress(connection.address.hostName, connection.address.port)
        val playerConnection = DefaultPlayerConnection(playerAddress, connection.name, connection.uniqueId, connection.isOnlineMode, connection.version)

        //send login request
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketOutCreateCloudPlayer(playerConnection, CloudPlugin.instance.thisServiceName)).awaitUninterruptibly()
    }

    @EventHandler
    fun on(event: PostLoginEvent) {
        val proxiedPlayer = event.player
        proxiedPlayer.reconnectServer = null
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketOutPlayerLoginRequest(proxiedPlayer.uniqueId)).addFailureListener {
            proxiedPlayer.disconnect(CloudTextBuilder().build(CloudText("§cLogin failed: " + it.message)))
        }

        //update service
        val thisService = CloudPlugin.instance.thisService()
        thisService.setOnlinePlayers(thisService.getOnlinePlayers() + 1)
        thisService.update()
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun on(event: PlayerDisconnectEvent) {
        println(event.player.uniqueId)
        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(event.player.uniqueId)
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
        thisService.setOnlinePlayers(thisService.getOnlinePlayers() - 1)
        thisService.update()
    }

    @EventHandler
    fun on(event: ServerConnectEvent) {
        val proxiedPlayer = event.player
        val target = event.target
        val cloudService = CloudAPI.instance.getCloudServiceManger().getCloudServiceByName(target.name)
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
        val service = CloudAPI.instance.getCloudServiceManger().getCloudServiceByName(event.server.info.name)
        if (service == null){
            proxiedPlayer.disconnect(CloudTextBuilder().build(CloudText("§cService does not exist.")))
            return
        }
        service.setOnlinePlayers(service.getOnlinePlayers() + 1)
        service.update()

        //update player
        val cloudPlayer = proxiedPlayer.getCloudPlayer()
        cloudPlayer as CloudPlayer
        cloudPlayer.setConnectedServerName(service.getName())
        cloudPlayer.update()
    }

    @EventHandler
    fun on(event: ServerDisconnectEvent) {
        val service = CloudAPI.instance.getCloudServiceManger().getCloudServiceByName(event.target.name) ?: return
        service.setOnlinePlayers(service.getOnlinePlayers() - 1)
        service.update()
    }

    @EventHandler
    fun on(event: ServerKickEvent) {
        if (event.kickReasonComponent.isNotEmpty() && event.kickReasonComponent[0].toLegacyText().contains("Outdated server") || event.kickReasonComponent[0].toLegacyText().contains("Outdated client")) {
            val cloudService = CloudAPI.instance.getCloudServiceManger().getCloudServiceByName(event.kickedFrom.name)
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



}