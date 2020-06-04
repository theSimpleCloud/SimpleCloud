/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
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

import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.api.exception.NoSuchServiceException
import eu.thesimplecloud.api.exception.PlayerConnectException
import eu.thesimplecloud.api.exception.UnreachableServiceException
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
import eu.thesimplecloud.plugin.proxy.bungee.CloudBungeePlugin
import eu.thesimplecloud.plugin.proxy.bungee.text.CloudTextBuilder
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.05.2020
 * Time: 22:07
 */
class CloudPlayerManagerBungee : AbstractServiceCloudPlayerManager() {

    override fun sendMessageToPlayer(cloudPlayer: ICloudPlayer, cloudText: CloudText): ICommunicationPromise<Unit> {
        if (cloudPlayer.getConnectedProxyName() != CloudPlugin.instance.thisServiceName) {
            return CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOSendMessageToCloudPlayer(cloudPlayer, cloudText))
        }

        getProxiedPlayerByCloudPlayer(cloudPlayer)?.sendMessage(CloudTextBuilder().build(cloudText))
        return CommunicationPromise.of(Unit)
    }

    override fun connectPlayer(cloudPlayer: ICloudPlayer, cloudService: ICloudService): ICommunicationPromise<ConnectionResponse> {
        require(getCachedCloudPlayer(cloudPlayer.getUniqueId()) === cloudPlayer) { "CloudPlayer must be the cached player." }
        if (cloudService.getServiceType() == ServiceType.PROXY) return CommunicationPromise.failed(IllegalArgumentException("Cannot send a player to a proxy service"))
        if (cloudPlayer.getConnectedServerName() == cloudService.getName()) return CommunicationPromise.of(ConnectionResponse(cloudPlayer.getUniqueId(), true))
        if (cloudPlayer.getConnectedProxyName() != CloudPlugin.instance.thisServiceName) {
            return CloudPlugin.instance.communicationClient.sendQuery(PacketIOConnectCloudPlayer(cloudPlayer, cloudService), 500)
        }

        val serverInfo = getServerInfoByCloudService(cloudService)
        serverInfo
                ?: return CommunicationPromise.failed(UnreachableServiceException("Service is not registered on player's proxy"))
        val proxiedPlayer = getProxiedPlayerByCloudPlayer(cloudPlayer)
        proxiedPlayer
                ?: return CommunicationPromise.failed(NoSuchElementException("Unable to find the player on the proxy service"))
        val communicationPromise = CommunicationPromise<ConnectionResponse>()
        proxiedPlayer.connect(serverInfo) { boolean, _ ->
            if (boolean)
                communicationPromise.trySuccess(ConnectionResponse(cloudPlayer.getUniqueId(), false))
            else
                communicationPromise.tryFailure(PlayerConnectException("Unable to connect the player to the service"))
        }
        return communicationPromise
    }

    override fun kickPlayer(cloudPlayer: ICloudPlayer, message: String) {
        if (cloudPlayer.getConnectedProxyName() != CloudPlugin.instance.thisServiceName) {
            CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOKickCloudPlayer(cloudPlayer, message))
            return
        }

        getProxiedPlayerByCloudPlayer(cloudPlayer)?.disconnect(CloudTextBuilder().build(CloudText(message)))
    }

    override fun sendTitle(cloudPlayer: ICloudPlayer, title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) {
        if (cloudPlayer.getConnectedProxyName() != CloudPlugin.instance.thisServiceName) {
            CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOSendTitleToCloudPlayer(cloudPlayer, title, subTitle, fadeIn, stay, fadeOut))
            return
        }

        val titleObj = ProxyServer.getInstance().createTitle()
        titleObj.title(CloudTextBuilder().build(CloudText(title)))
                .subTitle(CloudTextBuilder().build(CloudText(subTitle)))
                .fadeIn(fadeIn)
                .stay(stay)
                .fadeOut(fadeOut)
        getProxiedPlayerByCloudPlayer(cloudPlayer)?.sendTitle(titleObj)
    }

    override fun forcePlayerCommandExecution(cloudPlayer: ICloudPlayer, command: String) {
        if (cloudPlayer.getConnectedProxyName() != CloudPlugin.instance.thisServiceName) {
            CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOCloudPlayerForceCommandExecution(cloudPlayer, command))
            return
        }

        getProxiedPlayerByCloudPlayer(cloudPlayer)?.chat("/$command")
    }

    override fun sendActionbar(cloudPlayer: ICloudPlayer, actionbar: String) {
        if (cloudPlayer.getConnectedProxyName() != CloudPlugin.instance.thisServiceName) {
            CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOSendActionbarToCloudPlayer(cloudPlayer, actionbar))
            return
        }

        getProxiedPlayerByCloudPlayer(cloudPlayer)?.sendMessage(ChatMessageType.ACTION_BAR, CloudTextBuilder().build(CloudText(actionbar)))
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: SimpleLocation): ICommunicationPromise<Unit> {
        return CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOTeleportPlayer(cloudPlayer, location))
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: ServiceLocation): ICommunicationPromise<Unit> {
        if (location.getService() == null) return CommunicationPromise.failed(NoSuchServiceException("Service to connect the player to cannot be found."))
        return CloudPlugin.instance.communicationClient.sendUnitQuery(
                PacketOutTeleportOtherService(cloudPlayer.getUniqueId(), location.serviceName, location as SimpleLocation),
                1000
        )
    }

    override fun hasPermission(cloudPlayer: ICloudPlayer, permission: String): ICommunicationPromise<Boolean> {
        if (cloudPlayer.getConnectedProxyName() != CloudPlugin.instance.thisServiceName) {
            return CloudPlugin.instance.communicationClient.sendQuery(PacketIOPlayerHasPermission(cloudPlayer.getUniqueId(), permission), 400)
        }

        val proxiedPlayer = getProxiedPlayerByCloudPlayer(cloudPlayer)
        proxiedPlayer ?: return CommunicationPromise.failed(NoSuchPlayerException("Unable to find bungeecord player"))
        return CommunicationPromise.of(proxiedPlayer.hasPermission(permission))
    }

    override fun getLocationOfPlayer(cloudPlayer: ICloudPlayer): ICommunicationPromise<ServiceLocation> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetPlayerLocation(cloudPlayer))
    }

    override fun sendPlayerToLobby(cloudPlayer: ICloudPlayer): ICommunicationPromise<Unit> {
        if (CloudPlugin.instance.thisServiceName != cloudPlayer.getConnectedProxyName()) {
            return CloudPlugin.instance.communicationClient.sendQuery(PacketIOSendPlayerToLobby(cloudPlayer.getUniqueId()))
        }
        val proxiedPlayer = getProxiedPlayerByCloudPlayer(cloudPlayer)
                ?: return CommunicationPromise.failed(NoSuchPlayerException("Unable to find bungeecord player"))
        val serverInfo = CloudBungeePlugin.instance.lobbyConnector.getLobbyServer(proxiedPlayer)
        if (serverInfo == null) {
            proxiedPlayer.disconnect(CloudTextBuilder().build(CloudText("§cNo fallback server found")))
            return CommunicationPromise.failed(NoSuchServiceException("No fallback server found"))
        }
        proxiedPlayer.connect(serverInfo)
        return CommunicationPromise.of(Unit)
    }

    private fun getProxiedPlayerByCloudPlayer(cloudPlayer: ICloudPlayer): ProxiedPlayer? {
        return ProxyServer.getInstance().getPlayer(cloudPlayer.getUniqueId())
    }

    private fun getServerInfoByCloudService(cloudService: ICloudService): ServerInfo? {
        return ProxyServer.getInstance().getServerInfo(cloudService.getName())
    }

}