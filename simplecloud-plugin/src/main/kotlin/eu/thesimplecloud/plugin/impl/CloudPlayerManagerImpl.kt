package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.exception.*
import eu.thesimplecloud.api.location.ServiceLocation
import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.api.network.packets.player.*
import eu.thesimplecloud.api.player.AbstractCloudPlayerManager
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.plugin.extension.syncBukkit
import eu.thesimplecloud.plugin.network.packets.PacketOutTeleportOtherService
import eu.thesimplecloud.plugin.proxy.CloudProxyPlugin
import eu.thesimplecloud.plugin.proxy.LobbyConnector
import eu.thesimplecloud.plugin.proxy.text.CloudTextBuilder
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.*
import javax.xml.ws.Service

class CloudPlayerManagerImpl : AbstractCloudPlayerManager() {

    override fun updateCloudPlayer(cloudPlayer: ICloudPlayer, fromPacket: Boolean) {
        super.updateCloudPlayer(cloudPlayer, fromPacket)
        if (!fromPacket) CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOUpdateCloudPlayer(cloudPlayer))
    }

    override fun getCloudPlayer(uniqueId: UUID): ICommunicationPromise<ICloudPlayer> {
        val cachedCloudPlayer = getCachedCloudPlayer(uniqueId)
        if (cachedCloudPlayer != null) {
            return CommunicationPromise.of(cachedCloudPlayer)
        }
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetCloudPlayer(uniqueId))
    }

    override fun getCloudPlayer(name: String): ICommunicationPromise<ICloudPlayer> {
        val cachedCloudPlayer = getCachedCloudPlayer(name)
        if (cachedCloudPlayer != null) {
            return CommunicationPromise.of(cachedCloudPlayer)
        }
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetCloudPlayer(name))
    }

    override fun sendMessageToPlayer(cloudPlayer: ICloudPlayer, cloudText: CloudText) {
        if (cloudPlayer.getConnectedProxyName() == CloudPlugin.instance.thisServiceName) {
            getProxiedPlayerByCloudPlayer(cloudPlayer)?.sendMessage(CloudTextBuilder().build(cloudText))
            return
        }
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOSendMessageToCloudPlayer(cloudPlayer, cloudText))
    }

    override fun connectPlayer(cloudPlayer: ICloudPlayer, cloudService: ICloudService): ICommunicationPromise<Unit> {
        if (cloudService.getServiceType() == ServiceType.PROXY) return CommunicationPromise.failed(IllegalArgumentException("Cannot send a player to a proxy service"))
        if (cloudPlayer.getConnectedServerName() == cloudService.getName()) return CommunicationPromise.of(Unit)
        if (cloudPlayer.getConnectedProxyName() == CloudPlugin.instance.thisServiceName) {
            val serverInfo = getServerInfoByCloudService(cloudService)
            serverInfo
                    ?: return CommunicationPromise.failed(UnreachableServiceException("Service is not registered on player's proxy"))
            val proxiedPlayer = getProxiedPlayerByCloudPlayer(cloudPlayer)
            proxiedPlayer
                    ?: return CommunicationPromise.failed(NoSuchElementException("Unable to find the player on the proxy service"))
            val communicationPromise = CommunicationPromise<Unit>()
            proxiedPlayer.connect(serverInfo) { boolean, _ ->
                if (boolean) communicationPromise.trySuccess(Unit) else communicationPromise.tryFailure(PlayerConnectException("Unable to connect the player to the service"))
            }
            return communicationPromise
        }
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOConnectCloudPlayer(cloudPlayer, cloudService))
    }

    override fun kickPlayer(cloudPlayer: ICloudPlayer, message: String) {
        if (cloudPlayer.getConnectedProxyName() == CloudPlugin.instance.thisServiceName) {
            getProxiedPlayerByCloudPlayer(cloudPlayer)?.disconnect(CloudTextBuilder().build(CloudText(message)))
            return
        }
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOKickCloudPlayer(cloudPlayer, message))
    }

    override fun sendTitle(cloudPlayer: ICloudPlayer, title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) {
        if (cloudPlayer.getConnectedProxyName() == CloudPlugin.instance.thisServiceName) {
            val titleObj = ProxyServer.getInstance().createTitle()
            titleObj.title(CloudTextBuilder().build(CloudText(title)))
                    .subTitle(CloudTextBuilder().build(CloudText(subTitle)))
                    .fadeIn(fadeIn)
                    .stay(stay)
                    .fadeOut(fadeOut)
            getProxiedPlayerByCloudPlayer(cloudPlayer)?.sendTitle(titleObj)
            return
        }
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOSendTitleToCloudPlayer(cloudPlayer, title, subTitle, fadeIn, stay, fadeOut))
    }

    override fun forcePlayerCommandExecution(cloudPlayer: ICloudPlayer, command: String) {
        if (cloudPlayer.getConnectedProxyName() == CloudPlugin.instance.thisServiceName) {
            getProxiedPlayerByCloudPlayer(cloudPlayer)?.chat("/$command")
            return
        }
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOCloudPlayerForceCommandExecution(cloudPlayer, command))
    }

    override fun sendActionbar(cloudPlayer: ICloudPlayer, actionbar: String) {
        if (cloudPlayer.getConnectedProxyName() == CloudPlugin.instance.thisServiceName) {
            getProxiedPlayerByCloudPlayer(cloudPlayer)?.sendMessage(ChatMessageType.ACTION_BAR, CloudTextBuilder().build(CloudText(actionbar)))
            return
        }
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOSendActionbarToCloudPlayer(cloudPlayer, actionbar))
    }

    override fun setUpdates(cloudPlayer: ICloudPlayer, update: Boolean, serviceName: String) {
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOSetCloudPlayerUpdates(cloudPlayer, update, serviceName))
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: SimpleLocation): ICommunicationPromise<Unit> {
        if (CloudPlugin.instance.thisServiceName == cloudPlayer.getConnectedServerName()) {
            val bukkitPlayer = getBukkitPlayerByCloudPlayer(cloudPlayer)
            bukkitPlayer
                    ?: return CommunicationPromise.failed(NoSuchPlayerException("Unable to find the player on the server service"))
            val bukkitLocation = getBukkitLocationBySimpleLocation(location)
            bukkitLocation
                    ?: return CommunicationPromise.failed(NoSuchWorldException("Unable to find world: ${location.worldName}"))
            syncBukkit { bukkitPlayer.teleport(bukkitLocation) }
            return CommunicationPromise.of(Unit)
        }
        return CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOTeleportPlayer(cloudPlayer, location))
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: ServiceLocation): ICommunicationPromise<Unit> {
        if (location.getService() == null) return CommunicationPromise.failed(NoSuchServiceException("Service to connect the player to cannot be found."))
        return CloudPlugin.instance.communicationClient.sendUnitQuery(PacketOutTeleportOtherService(cloudPlayer.getUniqueId(), location.serviceName, location as SimpleLocation))
    }

    override fun hasPermission(cloudPlayer: ICloudPlayer, permission: String): ICommunicationPromise<Boolean> {
        val proxiedPlayer = getProxiedPlayerByCloudPlayer(cloudPlayer)
        proxiedPlayer ?: return CommunicationPromise.failed(NoSuchPlayerException("Unable to find bungeecord player"))
        return CommunicationPromise.of(proxiedPlayer.hasPermission(permission))
    }

    override fun getLocationOfPlayer(cloudPlayer: ICloudPlayer): ICommunicationPromise<ServiceLocation> {
        if (CloudPlugin.instance.thisServiceName == cloudPlayer.getConnectedServerName()) {
            val bukkitPlayer = getBukkitPlayerByCloudPlayer(cloudPlayer)
            bukkitPlayer ?: return CommunicationPromise.failed(NoSuchPlayerException("Unable to find bukkit player"))
            val playerLocation = bukkitPlayer.location
            playerLocation.world
                    ?: return CommunicationPromise.failed(NoSuchWorldException("The world the player is on is null"))
            return CommunicationPromise.of(ServiceLocation(CloudPlugin.instance.thisService(), playerLocation.world!!.name, playerLocation.x, playerLocation.y, playerLocation.z, playerLocation.yaw, playerLocation.pitch))
        }
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetPlayerLocation(cloudPlayer))
    }

    override fun sendPlayerToLobby(cloudPlayer: ICloudPlayer): ICommunicationPromise<Unit> {
        if (CloudPlugin.instance.thisServiceName == cloudPlayer.getConnectedProxyName()) {
            val proxiedPlayer = getProxiedPlayerByCloudPlayer(cloudPlayer)
                    ?: return CommunicationPromise.failed(NoSuchPlayerException("Unable to find bungeecord player"))
            val serverInfo = CloudProxyPlugin.instance.lobbyConnector.getLobbyServer(proxiedPlayer)
            if (serverInfo == null) {
                proxiedPlayer.disconnect(CloudTextBuilder().build(CloudText("§cNo fallback server found")))
                return CommunicationPromise.failed(NoSuchServiceException("No fallback server found"))
            }
            proxiedPlayer.connect(serverInfo)
            return CommunicationPromise.of(Unit)
        }
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOSendPlayerToLobby(cloudPlayer.getUniqueId()))
    }

    override fun getOfflineCloudPlayer(name: String): ICommunicationPromise<IOfflineCloudPlayer> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetOfflinePlayer(name))
    }

    override fun getOfflineCloudPlayer(uniqueId: UUID): ICommunicationPromise<IOfflineCloudPlayer> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetOfflinePlayer(uniqueId))
    }


    private fun getBukkitLocationBySimpleLocation(simpleLocation: SimpleLocation): Location? {
        val world = Bukkit.getWorld(simpleLocation.worldName) ?: return null
        return Location(world, simpleLocation.x, simpleLocation.y, simpleLocation.z, simpleLocation.yaw, simpleLocation.pitch)
    }

    private fun getBukkitPlayerByCloudPlayer(cloudPlayer: ICloudPlayer): Player? {
        return Bukkit.getPlayer(cloudPlayer.getUniqueId())
    }

    private fun getProxiedPlayerByCloudPlayer(cloudPlayer: ICloudPlayer): ProxiedPlayer? {
        return ProxyServer.getInstance().getPlayer(cloudPlayer.getUniqueId())
    }

    private fun getServerInfoByCloudService(cloudService: ICloudService): ServerInfo? {
        return ProxyServer.getInstance().getServerInfo(cloudService.getName())
    }
}