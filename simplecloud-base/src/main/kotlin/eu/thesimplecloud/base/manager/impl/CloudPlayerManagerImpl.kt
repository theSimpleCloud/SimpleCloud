package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.exception.NoSuchPlayerException
import eu.thesimplecloud.lib.exception.UnavailableServiceException
import eu.thesimplecloud.lib.location.ServiceLocation
import eu.thesimplecloud.lib.location.SimpleLocation
import eu.thesimplecloud.lib.network.packets.player.*
import eu.thesimplecloud.lib.player.AbstractCloudPlayerManager
import eu.thesimplecloud.lib.player.ICloudPlayer
import eu.thesimplecloud.lib.player.IOfflineCloudPlayer
import eu.thesimplecloud.lib.player.text.CloudText
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceType
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CloudPlayerManagerImpl : AbstractCloudPlayerManager() {

    /**
     * This maps contains player unique ids and service names.
     * The uuid is the unique id of the player.
     */
    val playerUpdates = HashMap<UUID, MutableList<String>>()

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
        val proxyClient = getProxyClientOfCloudPlayer(cloudPlayer)
        proxyClient ?: return CommunicationPromise.failed(UnavailableServiceException("Proxy service is unreachable"))
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

    override fun teleport(cloudPlayer: ICloudPlayer, location: SimpleLocation): ICommunicationPromise<Unit> {
        val serverClient = getServerClientOfPlayer(cloudPlayer)
        serverClient ?: return CommunicationPromise.failed(UnavailableServiceException("The server the player is connected to is not reachable"))
        return serverClient.sendUnitQuery(PacketIOTeleportPlayer(cloudPlayer, location))
    }

    override fun getLocationOfPlayer(cloudPlayer: ICloudPlayer): ICommunicationPromise<ServiceLocation> {
        val serverClient = getServerClientOfPlayer(cloudPlayer)
        serverClient ?: return CommunicationPromise.failed(UnavailableServiceException("The server the player is connected to is not reachable"))
        return serverClient.sendQuery<ServiceLocation>(PacketIOGetPlayerLocation(cloudPlayer))
    }

    override fun getOfflineCloudPlayer(name: String): ICommunicationPromise<IOfflineCloudPlayer> {
        val offlinePlayer = Manager.instance.offlineCloudPlayerLoader.getOfflinePlayer(name)
        return CommunicationPromise.ofNullable(offlinePlayer, NoSuchPlayerException("Player not found"))
    }

    override fun getOfflineCloudPlayer(uniqueId: UUID): ICommunicationPromise<IOfflineCloudPlayer> {
        val offlinePlayer = Manager.instance.offlineCloudPlayerLoader.getOfflinePlayer(uniqueId)
        return CommunicationPromise.ofNullable(offlinePlayer, NoSuchPlayerException("Player not found"))
    }

    private fun getProxyClientOfCloudPlayer(cloudPlayer: ICloudPlayer): IConnectedClient<*>? {
        return getCloudClientByServiceName(cloudPlayer.getConnectedProxyName())
    }

    private fun getServerClientOfPlayer(cloudPlayer: ICloudPlayer): IConnectedClient<*>? {
        return cloudPlayer.getConnectedServerName()?.let { getCloudClientByServiceName(it) }
    }

    private fun getCloudClientByServiceName(serviceName: String): IConnectedClient<*>? {
        val cloudService = CloudLib.instance.getCloudServiceManger().getCloudService(serviceName)
        return cloudService?.let { Manager.instance.communicationServer.getClientManager().getClientByClientValue(it) }
    }

}