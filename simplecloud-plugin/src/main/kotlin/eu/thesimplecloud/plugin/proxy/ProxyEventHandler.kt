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

package eu.thesimplecloud.plugin.proxy

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.player.CloudPlayerDisconnectEvent
import eu.thesimplecloud.api.event.player.CloudPlayerLoginEvent
import eu.thesimplecloud.api.player.CloudPlayer
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.player.PlayerServerConnectState
import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.plugin.network.packets.PacketOutCreateCloudPlayer
import eu.thesimplecloud.plugin.network.packets.PacketOutGetTabSuggestions
import eu.thesimplecloud.plugin.network.packets.PacketOutPlayerConnectToServer
import eu.thesimplecloud.plugin.network.packets.PacketOutPlayerLoginRequest
import eu.thesimplecloud.plugin.startup.CloudPlugin
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.05.2020
 * Time: 23:07
 */
object ProxyEventHandler {

    fun handleLogin(playerConnection: DefaultPlayerConnection, cancelEvent: (String) -> Unit) {
        val thisService = CloudPlugin.instance.thisService()
        if (!thisService.isAuthenticated() || !thisService.isOnline()) {
            cancelEvent("§cProxy is still starting.")
            return
        }
        val playerPromise = CloudAPI.instance.getCloudPlayerManager()
                .getCloudPlayer(playerConnection.getUniqueId()).awaitUninterruptibly()
        if (playerPromise.isSuccess) {
            handleAlreadyRegistered(playerPromise.getNow()!!)
        }

        //send login request
        val createPromise = CloudPlugin.instance.connectionToManager
                .sendQuery<CloudPlayer>(PacketOutCreateCloudPlayer(playerConnection, CloudPlugin.instance.thisServiceName), 1000).awaitUninterruptibly()
        if (!createPromise.isSuccess) {
            cancelEvent("§cFailed to create player: ${createPromise.cause().message}")
            println("Failed to create CloudPlayer:")
            throw createPromise.cause()
        }
        val loginRequestPromise = CloudPlugin.instance.connectionToManager.sendUnitQuery(PacketOutPlayerLoginRequest(playerConnection.getUniqueId())).awaitUninterruptibly()
        if (!loginRequestPromise.isSuccess) {
            loginRequestPromise.cause().printStackTrace()
            cancelEvent("§cLogin failed: " + loginRequestPromise.cause().message)
        }

        //update player to cache to avoid bugs
        CloudAPI.instance.getCloudPlayerManager().update(createPromise.getNow()!!, true)
    }

    private fun handleAlreadyRegistered(player: ICloudPlayer) {
        player.kick().awaitUninterruptibly()
        CloudAPI.instance.getCloudPlayerManager().sendDeleteToConnection(player, CloudPlugin.instance.connectionToManager)
                .awaitUninterruptibly()
    }

    fun handlePostLogin(uniqueId: UUID, name: String) {
        val thisService = CloudPlugin.instance.thisService()
        thisService.setOnlineCount(thisService.getOnlineCount() + 1)
        thisService.update()

        CloudAPI.instance.getEventManager().call(CloudPlayerLoginEvent(uniqueId, name))
    }


    fun handleDisconnect(uniqueId: UUID, name: String) {
        CloudAPI.instance.getEventManager().call(CloudPlayerDisconnectEvent(uniqueId, name))

        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(uniqueId)
        cloudPlayer?.let {
            cloudPlayer as CloudPlayer
            cloudPlayer.setOffline()
            CloudAPI.instance.getCloudPlayerManager().delete(cloudPlayer)
            //send update that the player is now offline
            val connection = CloudPlugin.instance.connectionToManager
            CloudAPI.instance.getCloudPlayerManager().sendDeleteToConnection(cloudPlayer, connection)
        }

        subtractOneFromOnlineCount(CloudPlugin.instance.thisService())
    }


    fun handleServerPreConnect(uniqueId: UUID, serverNameFrom: String?, serverNameTo: String, cancelEvent: (String, CancelType) -> Unit) {

        val cloudService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(serverNameTo)
        if (cloudService == null) {
            cancelEvent("§cServer is not registered in the cloud.", CancelType.KICK)
            return
        }
        val serviceGroup = cloudService.getServiceGroup()
        val cloudPlayer: ICloudPlayer? = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(uniqueId)

        if (cloudPlayer == null) {
            cancelEvent("§cUnable to find cloud player.", CancelType.KICK)
            return
        }

        if (serviceGroup.isInMaintenance()) {
            if (!cloudPlayer.hasPermissionSync("cloud.maintenance.join")) {
                cancelEvent("§cThis service is in maintenance.", CancelType.MESSAGE)
                return
            }
        }

        if (serviceGroup is ICloudServerGroup) {
            val permission = serviceGroup.getPermission()
            if (permission != null && !cloudPlayer.hasPermissionSync(permission)) {
                cancelEvent("§cYou don't have the permission to join this server.", CancelType.MESSAGE)
                return
            }
        }

        if (cloudService.getState() == ServiceState.STARTING) {
            cancelEvent("§cServer is still starting.", CancelType.MESSAGE)
            return
        }

        if (serverNameFrom != null) {
            subtractOneFromOnlineCount(serverNameFrom)
        }

        CloudPlugin.instance.connectionToManager.sendUnitQuery(PacketOutPlayerConnectToServer(uniqueId, serverNameTo))
                .awaitUninterruptibly()
                .addFailureListener {
                    cancelEvent("§cCan't connect to server: " + it.message, CancelType.MESSAGE)
                }


        //update player
        //use cloned player to compare connected server with old connected server.
        val clonedPlayer = cloudPlayer.clone() as CloudPlayer
        clonedPlayer.setConnectedServerName(cloudService.getName())
        clonedPlayer.setServerConnectState(PlayerServerConnectState.CONNECTING)
        clonedPlayer.update()
    }


    fun handleServerConnect(uniqueId: UUID, serverName: String, cancelEvent: (String) -> Unit) {
        val service = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(serverName)
        if (service == null) {
            cancelEvent("§cService does not exist.")
            return
        }
        service.setOnlineCount(service.getOnlineCount() + 1)
        service.update()

        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(uniqueId)
        cloudPlayer ?: return
        val clonedPlayer = cloudPlayer.clone() as CloudPlayer
        clonedPlayer.setServerConnectState(PlayerServerConnectState.CONNECTED)
        clonedPlayer.update()
    }

    fun handleServerKick(kickReasonString: String, serverName: String, cancelEvent: (String, CancelType) -> Unit) {
        if (kickReasonString.isNotEmpty() && kickReasonString.contains("Outdated server") || kickReasonString.contains("Outdated client")) {
            val cloudService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(serverName)
            if (cloudService == null || cloudService.isLobby()) {
                cancelEvent("§cYou are using an unsupported version.", CancelType.KICK)
                return
            }
        }
    }

    fun handleTabComplete(uuid: UUID, rawCommand: String): Array<String> {
        val commandString = rawCommand.replace("/", "")
        if (commandString.isEmpty()) return emptyArray()

        val suggestions = CloudPlugin.instance.connectionToManager.sendQuery<Array<String>>(PacketOutGetTabSuggestions(uuid, commandString)).getBlocking()
        return suggestions
    }

    private fun subtractOneFromOnlineCount(serverName: String) {
        val service = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(serverName) ?: return
        subtractOneFromOnlineCount(service)
    }

    private fun subtractOneFromOnlineCount(service: ICloudService) {
        service.setOnlineCount(service.getOnlineCount() - 1)
        service.update()
    }

}