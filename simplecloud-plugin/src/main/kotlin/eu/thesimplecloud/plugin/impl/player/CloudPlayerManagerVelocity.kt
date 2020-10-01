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

package eu.thesimplecloud.plugin.impl.player

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.api.exception.NoSuchServiceException
import eu.thesimplecloud.api.exception.PlayerConnectException
import eu.thesimplecloud.api.exception.UnreachableComponentException
import eu.thesimplecloud.api.location.ServiceLocation
import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.api.network.packets.player.*
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.player.connection.ConnectionResponse
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.plugin.network.packets.PacketOutTeleportOtherService
import eu.thesimplecloud.plugin.proxy.velocity.CloudVelocityPlugin
import eu.thesimplecloud.plugin.proxy.velocity.text.CloudTextBuilder
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.kyori.adventure.title.Title
import net.kyori.adventure.util.Ticks
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.05.2020
 * Time: 22:07
 */
class CloudPlayerManagerVelocity : AbstractServiceCloudPlayerManager() {

    override fun sendMessageToPlayer(cloudPlayer: ICloudPlayer, cloudText: CloudText): ICommunicationPromise<Unit> {
        if (cloudPlayer.getConnectedProxyName() != CloudPlugin.instance.thisServiceName) {
            return CloudPlugin.instance.connectionToManager.sendUnitQuery(PacketIOSendMessageToCloudPlayer(cloudPlayer, cloudText))
        }

        getPlayerByCloudPlayer(cloudPlayer)?.sendMessage(CloudTextBuilder().build(cloudText))
        return CommunicationPromise.of(Unit)
    }

    override fun connectPlayer(cloudPlayer: ICloudPlayer, cloudService: ICloudService): ICommunicationPromise<ConnectionResponse> {
        require(getCachedCloudPlayer(cloudPlayer.getUniqueId()) === cloudPlayer) { "CloudPlayer must be the cached player." }
        if (cloudService.getServiceType() == ServiceType.PROXY) return CommunicationPromise.failed(IllegalArgumentException("Cannot send a player to a proxy service"))
        if (cloudPlayer.getConnectedServerName() == cloudService.getName()) return CommunicationPromise.of(ConnectionResponse(cloudPlayer.getUniqueId(), true))
        if (cloudPlayer.getConnectedProxyName() != CloudPlugin.instance.thisServiceName) {
            return CloudPlugin.instance.connectionToManager.sendQuery(PacketIOConnectCloudPlayer(cloudPlayer, cloudService), 500)
        }

        val server = getServerInfoByCloudService(cloudService)
        server
                ?: return CommunicationPromise.failed(UnreachableComponentException("Service is not registered on player's proxy"))
        val player = getPlayerByCloudPlayer(cloudPlayer)
        player
                ?: return CommunicationPromise.failed(NoSuchElementException("Unable to find the player on the proxy service"))
        val communicationPromise = CommunicationPromise<ConnectionResponse>()
        player.createConnectionRequest(server).connectWithIndication().thenAccept {
            if (it)
                communicationPromise.trySuccess(ConnectionResponse(cloudPlayer.getUniqueId(), false))
            else
                communicationPromise.tryFailure(PlayerConnectException("Unable to connect the player to the service"))
        }
        return communicationPromise
    }

    override fun kickPlayer(cloudPlayer: ICloudPlayer, message: String): ICommunicationPromise<Unit> {
        if (cloudPlayer.getConnectedProxyName() != CloudPlugin.instance.thisServiceName) {
            return CloudPlugin.instance.connectionToManager.sendUnitQuery(PacketIOKickCloudPlayer(cloudPlayer, message))
        }

        getPlayerByCloudPlayer(cloudPlayer)?.disconnect(CloudTextBuilder().build(CloudText(message)))
        return CommunicationPromise.of(Unit)
    }

    override fun sendTitle(cloudPlayer: ICloudPlayer, title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) {
        if (cloudPlayer.getConnectedProxyName() != CloudPlugin.instance.thisServiceName) {
            CloudPlugin.instance.connectionToManager.sendUnitQuery(PacketIOSendTitleToCloudPlayer(cloudPlayer, title, subTitle, fadeIn, stay, fadeOut))
            return
        }


        val titleObj = Title.of(
                CloudTextBuilder().build(CloudText(title)),
                CloudTextBuilder().build(CloudText(subTitle)),
                Title.Times.of(
                        Ticks.duration(fadeIn.toLong()),
                        Ticks.duration(stay.toLong()),
                        Ticks.duration(fadeOut.toLong())
                )
        )

        getPlayerByCloudPlayer(cloudPlayer)?.showTitle(titleObj)
    }

    override fun forcePlayerCommandExecution(cloudPlayer: ICloudPlayer, command: String) {
        println("1 spoofing input $command")
        if (cloudPlayer.getConnectedProxyName() != CloudPlugin.instance.thisServiceName) {
            CloudPlugin.instance.connectionToManager.sendUnitQuery(PacketIOCloudPlayerForceCommandExecution(cloudPlayer, command))
            return
        }
        println("2 spoofing input $command")
        getPlayerByCloudPlayer(cloudPlayer)?.spoofChatInput("/$command")
    }

    override fun sendActionbar(cloudPlayer: ICloudPlayer, actionbar: String) {
        if (cloudPlayer.getConnectedProxyName() != CloudPlugin.instance.thisServiceName) {
            CloudPlugin.instance.connectionToManager.sendUnitQuery(PacketIOSendActionbarToCloudPlayer(cloudPlayer, actionbar))
            return
        }
        getPlayerByCloudPlayer(cloudPlayer)?.sendActionBar(CloudTextBuilder().build(CloudText(actionbar)))
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: SimpleLocation): ICommunicationPromise<Unit> {
        return CloudPlugin.instance.connectionToManager.sendUnitQuery(PacketIOTeleportPlayer(cloudPlayer, location))
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: ServiceLocation): ICommunicationPromise<Unit> {
        if (location.getService() == null) return CommunicationPromise.failed(NoSuchServiceException("Service to connect the player to cannot be found."))
        return CloudPlugin.instance.connectionToManager.sendUnitQuery(
                PacketOutTeleportOtherService(cloudPlayer.getUniqueId(), location.serviceName, location as SimpleLocation),
                1000
        )
    }

    override fun hasPermission(cloudPlayer: ICloudPlayer, permission: String): ICommunicationPromise<Boolean> {
        if (cloudPlayer.getConnectedProxyName() != CloudPlugin.instance.thisServiceName) {
            return CloudPlugin.instance.connectionToManager.sendQuery(PacketIOPlayerHasPermission(cloudPlayer.getUniqueId(), permission), 400)
        }

        val player = getPlayerByCloudPlayer(cloudPlayer)
        player ?: return CommunicationPromise.failed(NoSuchPlayerException("Unable to find bungeecord player"))
        return CommunicationPromise.of(player.hasPermission(permission))
    }

    override fun getLocationOfPlayer(cloudPlayer: ICloudPlayer): ICommunicationPromise<ServiceLocation> {
        return CloudPlugin.instance.connectionToManager.sendQuery(PacketIOGetPlayerLocation(cloudPlayer))
    }

    override fun sendPlayerToLobby(cloudPlayer: ICloudPlayer): ICommunicationPromise<Unit> {
        if (CloudPlugin.instance.thisServiceName != cloudPlayer.getConnectedProxyName()) {
            return CloudPlugin.instance.connectionToManager.sendQuery(PacketIOSendPlayerToLobby(cloudPlayer.getUniqueId()))
        }
        val player = getPlayerByCloudPlayer(cloudPlayer)
                ?: return CommunicationPromise.failed(NoSuchPlayerException("Unable to find bungeecord player"))
        val server = CloudVelocityPlugin.instance.lobbyConnector.getLobbyServer(player)
        if (server == null) {
            player.disconnect(CloudTextBuilder().build(CloudText("§cNo fallback server found")))
            return CommunicationPromise.failed(NoSuchServiceException("No fallback server found"))
        }
        player.createConnectionRequest(server).fireAndForget()
        return CommunicationPromise.of(Unit)
    }

    private fun getPlayerByCloudPlayer(cloudPlayer: ICloudPlayer): Player? {
        return CloudVelocityPlugin.instance.proxyServer.getPlayer(cloudPlayer.getUniqueId()).orElse(null)
    }

    private fun getServerInfoByCloudService(cloudService: ICloudService): RegisteredServer? {
        return CloudVelocityPlugin.instance.proxyServer.getServer(cloudService.getName()).orElse(null)
    }

}