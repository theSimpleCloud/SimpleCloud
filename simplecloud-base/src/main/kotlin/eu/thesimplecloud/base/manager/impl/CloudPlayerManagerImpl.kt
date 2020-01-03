package eu.thesimplecloud.base.manager.impl

import com.google.common.collect.Maps
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.api.exception.UnreachableServiceException
import eu.thesimplecloud.api.location.ServiceLocation
import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.api.network.packets.player.*
import eu.thesimplecloud.api.player.AbstractCloudPlayerManager
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceType
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CloudPlayerManagerImpl : AbstractCloudPlayerManager() {

    /**
     * This maps contains player unique ids and service names.
     * The uuid is the unique id of the player.
     */
    val playerUpdates = Maps.newConcurrentMap<UUID, MutableList<String>>()

    override fun updateCloudPlayer(cloudPlayer: ICloudPlayer) {
        super.updateCloudPlayer(cloudPlayer)

        val proxyClient = cloudPlayer.getConnectedProxy()?.let { Manager.instance.communicationServer.getClientManager().getClientByClientValue(it) }
        val serverClient = cloudPlayer.getConnectedServer()?.let { Manager.instance.communicationServer.getClientManager().getClientByClientValue(it) }
        val playerUpdatePacket = PacketIOUpdateCloudPlayer(cloudPlayer)

        proxyClient?.sendUnitQuery(playerUpdatePacket)
        serverClient?.sendUnitQuery(playerUpdatePacket)

        val requestedPlayerUpdatesServices = playerUpdates[cloudPlayer.getUniqueId()]
        requestedPlayerUpdatesServices?.mapNotNull { getCloudClientByServiceName(it) }?.forEach { it.sendUnitQuery(playerUpdatePacket) }
    }

    override fun removeCloudPlayer(cloudPlayer: ICloudPlayer) {
        super.removeCloudPlayer(cloudPlayer)

        val proxyClient = cloudPlayer.getConnectedProxy()?.let { Manager.instance.communicationServer.getClientManager().getClientByClientValue(it) }
        val serverClient = cloudPlayer.getConnectedServer()?.let { Manager.instance.communicationServer.getClientManager().getClientByClientValue(it) }
        val playerRemovePacket = PacketIORemoveCloudPlayer(cloudPlayer.getUniqueId())
        proxyClient?.sendUnitQuery(playerRemovePacket)
        serverClient?.sendUnitQuery(playerRemovePacket)

        val requestedPlayerUpdatesServices = playerUpdates[cloudPlayer.getUniqueId()]
        requestedPlayerUpdatesServices?.mapNotNull { getCloudClientByServiceName(it) }?.forEach { it.sendUnitQuery(playerRemovePacket) }

        playerUpdates.remove(cloudPlayer.getUniqueId())
    }

    override fun getCloudPlayer(uniqueId: UUID): ICommunicationPromise<ICloudPlayer> {
        return promiseOfNullablePlayer(getCachedCloudPlayer(uniqueId))
    }

    override fun getCloudPlayer(name: String): ICommunicationPromise<ICloudPlayer> {
        return promiseOfNullablePlayer(getCachedCloudPlayer(name))
    }

    override fun sendMessageToPlayer(cloudPlayer: ICloudPlayer, cloudText: CloudText) {
        val proxyClient = getProxyClientOfCloudPlayer(cloudPlayer)
        proxyClient?.sendUnitQuery(PacketIOSendMessageToCloudPlayer(cloudPlayer, cloudText))
    }

    override fun connectPlayer(cloudPlayer: ICloudPlayer, cloudService: ICloudService): ICommunicationPromise<Unit> {
        if (cloudService.getServiceType() == ServiceType.PROXY) return CommunicationPromise.failed(IllegalArgumentException("Cannot send player to a proxy service"))
        if (cloudPlayer.getConnectedServerName() == cloudService.getName()) return CommunicationPromise.of(Unit)
        val proxyClient = getProxyClientOfCloudPlayer(cloudPlayer)
        proxyClient ?: return CommunicationPromise.failed(UnreachableServiceException("Proxy service is unreachable"))
        return proxyClient.sendUnitQuery(PacketIOConnectCloudPlayer(cloudPlayer, cloudService))
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
        proxyClient?.sendUnitQuery(PacketIOCloudPlayerForceCommandExecution())
    }

    override fun sendActionbar(cloudPlayer: ICloudPlayer, actionbar: String) {
        val proxyClient = getProxyClientOfCloudPlayer(cloudPlayer)
        proxyClient?.sendUnitQuery(PacketIOSendActionbarToCloudPlayer(cloudPlayer, actionbar))
    }

    override fun setUpdates(cloudPlayer: ICloudPlayer, update: Boolean, serviceName: String) {
        val list = playerUpdates.getOrPut(cloudPlayer.getUniqueId()) { ArrayList() }
        if (update) list.add(serviceName) else list.remove(serviceName)
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: SimpleLocation): ICommunicationPromise<Unit> {
        val serverClient = getServerClientOfPlayer(cloudPlayer)
        serverClient ?: return CommunicationPromise.failed(UnreachableServiceException("The server the player is connected to is not reachable"))
        return serverClient.sendUnitQuery(PacketIOTeleportPlayer(cloudPlayer, location))
    }

    override fun hasPermission(cloudPlayer: ICloudPlayer, permission: String): ICommunicationPromise<Boolean> {
        val proxyClient = getProxyClientOfCloudPlayer(cloudPlayer)
        proxyClient ?: return CommunicationPromise.failed(UnreachableServiceException("The proxy the player is connected to is not reachable"))
        return proxyClient.sendQuery(PacketIOPlayerHasPermission(cloudPlayer.getUniqueId(), permission))
    }

    override fun getLocationOfPlayer(cloudPlayer: ICloudPlayer): ICommunicationPromise<ServiceLocation> {
        val serverClient = getServerClientOfPlayer(cloudPlayer)
        serverClient ?: return CommunicationPromise.failed(UnreachableServiceException("The server the player is connected to is not reachable"))
        return serverClient.sendQuery<ServiceLocation>(PacketIOGetPlayerLocation(cloudPlayer))
    }

    override fun sendPlayerToLobby(cloudPlayer: ICloudPlayer): ICommunicationPromise<Unit> {
        val proxyClient = getProxyClientOfCloudPlayer(cloudPlayer)
        proxyClient ?: return CommunicationPromise.failed(UnreachableServiceException("The proxy the player is connected to is not reachable"))
        return proxyClient.sendQuery(PacketIOSendPlayerToLobby(cloudPlayer.getUniqueId()))
    }

    override fun getOfflineCloudPlayer(name: String): ICommunicationPromise<IOfflineCloudPlayer> {
        val offlinePlayer = Manager.instance.offlineCloudPlayerLoader.getOfflinePlayer(name)
        return CommunicationPromise.ofNullable(offlinePlayer, NoSuchPlayerException("Player not found"))
    }

    override fun getOfflineCloudPlayer(uniqueId: UUID): ICommunicationPromise<IOfflineCloudPlayer> {
        val offlinePlayer = Manager.instance.offlineCloudPlayerLoader.getOfflinePlayer(uniqueId)
        return CommunicationPromise.ofNullable(offlinePlayer, NoSuchPlayerException("Player not found"))
    }

    override fun updateToNetwork(cloudPlayer: ICloudPlayer) {
        updateCloudPlayer(cloudPlayer)
    }

    private fun getProxyClientOfCloudPlayer(cloudPlayer: ICloudPlayer): IConnectedClient<*>? {
        return getCloudClientByServiceName(cloudPlayer.getConnectedProxyName())
    }

    private fun getServerClientOfPlayer(cloudPlayer: ICloudPlayer): IConnectedClient<*>? {
        return cloudPlayer.getConnectedServerName()?.let { getCloudClientByServiceName(it) }
    }

    private fun getCloudClientByServiceName(serviceName: String): IConnectedClient<*>? {
        val cloudService = CloudAPI.instance.getCloudServiceManger().getCloudServiceByName(serviceName)
        return cloudService?.let { Manager.instance.communicationServer.getClientManager().getClientByClientValue(it) }
    }

}