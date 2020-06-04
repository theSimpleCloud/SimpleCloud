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

package eu.thesimplecloud.base.manager.impl

import com.google.common.collect.Maps
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.api.exception.NoSuchServiceException
import eu.thesimplecloud.api.exception.UnreachableServiceException
import eu.thesimplecloud.api.location.ServiceLocation
import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.api.network.packets.player.*
import eu.thesimplecloud.api.network.packets.sync.cachelist.PacketIOUpdateCacheObject
import eu.thesimplecloud.api.player.*
import eu.thesimplecloud.api.player.connection.ConnectionResponse
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.flatten
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import java.util.*
import kotlin.collections.ArrayList

class CloudPlayerManagerImpl : AbstractCloudPlayerManager() {

    /**
     * This maps contains player unique ids and service names.
     * The uuid is the unique id of the player.
     */
    private val playerUpdates = Maps.newConcurrentMap<UUID, MutableList<String>>()

    override fun update(value: ICloudPlayer, fromPacket: Boolean, isCalledFromDelete: Boolean) {
        super.update(value, fromPacket, isCalledFromDelete)

        val proxyClient = value.getConnectedProxy()?.let { Manager.instance.communicationServer.getClientManager().getClientByClientValue(it) }
        val serverClient = value.getConnectedServer()?.let { Manager.instance.communicationServer.getClientManager().getClientByClientValue(it) }
        val playerUpdatePacket = PacketIOUpdateCacheObject(getUpdater().getIdentificationName(), value, PacketIOUpdateCacheObject.Action.UPDATE)

        if (proxyClient?.isOpen() == true)
            proxyClient.sendUnitQuery(playerUpdatePacket)
        if (serverClient?.isOpen() == true)
            serverClient.sendUnitQuery(playerUpdatePacket)

        val requestedPlayerUpdatesServices = playerUpdates[value.getUniqueId()]
        requestedPlayerUpdatesServices?.mapNotNull { getCloudClientByServiceName(it) }?.forEach { it.sendUnitQuery(playerUpdatePacket) }
    }

    override fun delete(value: ICloudPlayer, fromPacket: Boolean) {
        super.delete(value, fromPacket)

        val proxyClient = value.getConnectedProxy()?.let { Manager.instance.communicationServer.getClientManager().getClientByClientValue(it) }
        val serverClient = value.getConnectedServer()?.let { Manager.instance.communicationServer.getClientManager().getClientByClientValue(it) }
        val playerRemovePacket = PacketIOUpdateCacheObject(getUpdater().getIdentificationName(), value, PacketIOUpdateCacheObject.Action.DELETE)
        proxyClient?.sendUnitQuery(playerRemovePacket)
        serverClient?.sendUnitQuery(playerRemovePacket)

        val requestedPlayerUpdatesServices = playerUpdates[value.getUniqueId()]
        requestedPlayerUpdatesServices?.mapNotNull { getCloudClientByServiceName(it) }?.forEach { it.sendUnitQuery(playerRemovePacket) }

        playerUpdates.remove(value.getUniqueId())
        Manager.instance.offlineCloudPlayerHandler.saveCloudPlayer(value.toOfflinePlayer() as OfflineCloudPlayer)
    }

    override fun getCloudPlayer(uniqueId: UUID): ICommunicationPromise<ICloudPlayer> {
        return promiseOfNullablePlayer(getCachedCloudPlayer(uniqueId))
    }

    override fun getCloudPlayer(name: String): ICommunicationPromise<ICloudPlayer> {
        return promiseOfNullablePlayer(getCachedCloudPlayer(name))
    }

    override fun sendMessageToPlayer(cloudPlayer: ICloudPlayer, cloudText: CloudText): ICommunicationPromise<Unit> {
        val proxyClient = getProxyClientOfCloudPlayer(cloudPlayer)
        return proxyClient?.sendUnitQuery(PacketIOSendMessageToCloudPlayer(cloudPlayer, cloudText))
                ?: CommunicationPromise.failed(UnreachableServiceException("Proxy service is unreachable"))
    }

    override fun connectPlayer(cloudPlayer: ICloudPlayer, cloudService: ICloudService): ICommunicationPromise<ConnectionResponse> {
        if (cloudService.getServiceType() == ServiceType.PROXY) return CommunicationPromise.failed(IllegalArgumentException("Cannot send player to a proxy service"))
        if (cloudPlayer.getConnectedServerName() == cloudService.getName()) return CommunicationPromise.of(ConnectionResponse(cloudPlayer.getUniqueId(), true))
        val proxyClient = getProxyClientOfCloudPlayer(cloudPlayer)
        proxyClient ?: return CommunicationPromise.failed(UnreachableServiceException("Proxy service is unreachable"))
        return proxyClient.sendQuery(PacketIOConnectCloudPlayer(cloudPlayer, cloudService), 500)
    }

    override fun kickPlayer(cloudPlayer: ICloudPlayer, message: String) {
        val proxyClient = getProxyClientOfCloudPlayer(cloudPlayer)
        proxyClient?.sendUnitQuery(PacketIOKickCloudPlayer(cloudPlayer, message))
    }

    override fun sendTitle(cloudPlayer: ICloudPlayer, title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) {
        val proxyClient = getProxyClientOfCloudPlayer(cloudPlayer)
        proxyClient?.sendUnitQuery(PacketIOSendTitleToCloudPlayer(cloudPlayer, title, subTitle, fadeIn, stay, fadeOut))
    }

    override fun forcePlayerCommandExecution(cloudPlayer: ICloudPlayer, command: String) {
        val proxyClient = getProxyClientOfCloudPlayer(cloudPlayer)
        proxyClient?.sendUnitQuery(PacketIOCloudPlayerForceCommandExecution(cloudPlayer, command))
    }

    override fun sendActionbar(cloudPlayer: ICloudPlayer, actionbar: String) {
        val proxyClient = getProxyClientOfCloudPlayer(cloudPlayer)
        proxyClient?.sendUnitQuery(PacketIOSendActionbarToCloudPlayer(cloudPlayer, actionbar))
    }

    override fun setUpdates(cloudPlayer: ICloudPlayer, update: Boolean, serviceName: String) {
        super.setUpdates(cloudPlayer, update, serviceName)
        if (!update && !this.playerUpdates.containsKey(cloudPlayer.getUniqueId())) return
        val list = this.playerUpdates.getOrPut(cloudPlayer.getUniqueId()) { ArrayList() }
        if (update) list.add(serviceName) else list.remove(serviceName)
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: SimpleLocation): ICommunicationPromise<Unit> {
        val serverClient = getServerClientOfPlayer(cloudPlayer)
        serverClient
                ?: return CommunicationPromise.failed(UnreachableServiceException("The server the player is connected to is not reachable"))
        return serverClient.sendUnitQuery(PacketIOTeleportPlayer(cloudPlayer, location))
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: ServiceLocation): ICommunicationPromise<Unit> {
        val service = location.getService()
                ?: return CommunicationPromise.failed(NoSuchServiceException("Service to connect the player to cannot be found"))
        return if (service.getName() == cloudPlayer.getConnectedServerName()) {
            cloudPlayer.teleport(location as SimpleLocation).addFailureListener { cloudPlayer.sendMessage("§cTeleportation failed: " + it.message) }
        } else {
            cloudPlayer.connect(service).then {
                it.createConnectedPromise()
            }.flatten(3000).then {
                cloudPlayer.teleport(location as SimpleLocation)
            }.flatten(1500).addFailureListener { cloudPlayer.sendMessage("§cTeleportation failed: " + it.message) }
        }
    }

    override fun hasPermission(cloudPlayer: ICloudPlayer, permission: String): ICommunicationPromise<Boolean> {
        val proxyClient = getProxyClientOfCloudPlayer(cloudPlayer)
        proxyClient
                ?: return CommunicationPromise.failed(UnreachableServiceException("The proxy the player is connected to is not reachable"))
        return proxyClient.sendQuery(PacketIOPlayerHasPermission(cloudPlayer.getUniqueId(), permission))
    }

    override fun getLocationOfPlayer(cloudPlayer: ICloudPlayer): ICommunicationPromise<ServiceLocation> {
        val serverClient = getServerClientOfPlayer(cloudPlayer)
        serverClient
                ?: return CommunicationPromise.failed(UnreachableServiceException("The server the player is connected to is not reachable"))
        return serverClient.sendQuery<ServiceLocation>(PacketIOGetPlayerLocation(cloudPlayer))
    }

    override fun sendPlayerToLobby(cloudPlayer: ICloudPlayer): ICommunicationPromise<Unit> {
        val proxyClient = getProxyClientOfCloudPlayer(cloudPlayer)
        proxyClient
                ?: return CommunicationPromise.failed(UnreachableServiceException("The proxy the player is connected to is not reachable"))
        return proxyClient.sendQuery(PacketIOSendPlayerToLobby(cloudPlayer.getUniqueId()))
    }

    override fun getOfflineCloudPlayer(name: String): ICommunicationPromise<IOfflineCloudPlayer> {
        val onlineCloudPlayer = getCachedCloudPlayer(name)
        if (onlineCloudPlayer != null) return CommunicationPromise.of(onlineCloudPlayer)
        val offlinePlayer = Manager.instance.offlineCloudPlayerHandler.getOfflinePlayer(name)
        return CommunicationPromise.ofNullable(offlinePlayer, NoSuchPlayerException("Player not found"))
    }

    override fun getOfflineCloudPlayer(uniqueId: UUID): ICommunicationPromise<IOfflineCloudPlayer> {
        val onlineCloudPlayer = getCachedCloudPlayer(uniqueId)
        if (onlineCloudPlayer != null) return CommunicationPromise.of(onlineCloudPlayer)
        val offlinePlayer = Manager.instance.offlineCloudPlayerHandler.getOfflinePlayer(uniqueId)
        return CommunicationPromise.ofNullable(offlinePlayer, NoSuchPlayerException("Player not found"))
    }

    override fun getAllOnlinePlayers(): ICommunicationPromise<List<SimpleCloudPlayer>> {
        return CommunicationPromise.of(getAllCachedObjects().map { it.toSimplePlayer() })
    }

    private fun getProxyClientOfCloudPlayer(cloudPlayer: ICloudPlayer): IConnectedClient<*>? {
        return getCloudClientByServiceName(cloudPlayer.getConnectedProxyName())
    }

    private fun getServerClientOfPlayer(cloudPlayer: ICloudPlayer): IConnectedClient<*>? {
        return cloudPlayer.getConnectedServerName()?.let { getCloudClientByServiceName(it) }
    }

    private fun getCloudClientByServiceName(serviceName: String): IConnectedClient<*>? {
        val cloudService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(serviceName)
        return cloudService?.let { Manager.instance.communicationServer.getClientManager().getClientByClientValue(it) }
    }

    /**
     * Resets the requested player updates for the specified [serviceName]
     */
    fun resetPlayerUpdates(serviceName: String) {
        this.playerUpdates.values.forEach { it.remove(serviceName) }
    }

}