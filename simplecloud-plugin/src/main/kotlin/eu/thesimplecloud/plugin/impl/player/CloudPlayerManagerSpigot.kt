package eu.thesimplecloud.plugin.impl.player

import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.api.exception.NoSuchServiceException
import eu.thesimplecloud.api.exception.NoSuchWorldException
import eu.thesimplecloud.api.location.ServiceLocation
import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.api.network.packets.player.*
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.plugin.extension.syncBukkit
import eu.thesimplecloud.plugin.network.packets.PacketOutTeleportOtherService
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.05.2020
 * Time: 21:58
 */
class CloudPlayerManagerSpigot : AbstractServiceCloudPlayerManager() {

    override fun sendMessageToPlayer(cloudPlayer: ICloudPlayer, cloudText: CloudText): ICommunicationPromise<Unit> {
        return CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOSendMessageToCloudPlayer(cloudPlayer, cloudText))
    }

    override fun connectPlayer(cloudPlayer: ICloudPlayer, cloudService: ICloudService): ICommunicationPromise<Unit> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOConnectCloudPlayer(cloudPlayer, cloudService))
    }

    override fun kickPlayer(cloudPlayer: ICloudPlayer, message: String) {
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOKickCloudPlayer(cloudPlayer, message))
    }

    override fun sendTitle(cloudPlayer: ICloudPlayer, title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) {
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOSendTitleToCloudPlayer(cloudPlayer, title, subTitle, fadeIn, stay, fadeOut))
    }

    override fun forcePlayerCommandExecution(cloudPlayer: ICloudPlayer, command: String) {
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOCloudPlayerForceCommandExecution(cloudPlayer, command))
    }

    override fun sendActionbar(cloudPlayer: ICloudPlayer, actionbar: String) {
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOSendActionbarToCloudPlayer(cloudPlayer, actionbar))
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: SimpleLocation): ICommunicationPromise<Unit> {
        if (CloudPlugin.instance.thisServiceName != cloudPlayer.getConnectedServerName()) {
            return CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOTeleportPlayer(cloudPlayer, location))
        }

        val bukkitPlayer = getPlayerByCloudPlayer(cloudPlayer)
        bukkitPlayer
                ?: return CommunicationPromise.failed(NoSuchPlayerException("Unable to find the player on the server service"))
        val bukkitLocation = getLocationBySimpleLocation(location)
        bukkitLocation
                ?: return CommunicationPromise.failed(NoSuchWorldException("Unable to find world: ${location.worldName}"))
        syncBukkit { bukkitPlayer.teleport(bukkitLocation) }
        return CommunicationPromise.of(Unit)
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: ServiceLocation): ICommunicationPromise<Unit> {
        if (location.getService() == null) return CommunicationPromise.failed(NoSuchServiceException("Service to connect the player to cannot be found."))
        return CloudPlugin.instance.communicationClient.sendUnitQuery(PacketOutTeleportOtherService(cloudPlayer.getUniqueId(), location.serviceName, location as SimpleLocation))
    }

    override fun hasPermission(cloudPlayer: ICloudPlayer, permission: String): ICommunicationPromise<Boolean> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOPlayerHasPermission(cloudPlayer.getUniqueId(), permission), 400)
    }

    override fun getLocationOfPlayer(cloudPlayer: ICloudPlayer): ICommunicationPromise<ServiceLocation> {
        if (CloudPlugin.instance.thisServiceName != cloudPlayer.getConnectedServerName()) {
            return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetPlayerLocation(cloudPlayer))
        }

        val bukkitPlayer = getPlayerByCloudPlayer(cloudPlayer)
        bukkitPlayer ?: return CommunicationPromise.failed(NoSuchPlayerException("Unable to find bukkit player"))
        val playerLocation = bukkitPlayer.location
        playerLocation.world
                ?: return CommunicationPromise.failed(NoSuchWorldException("The world the player is on is null"))
        return CommunicationPromise.of(ServiceLocation(CloudPlugin.instance.thisService(), playerLocation.world!!.name, playerLocation.x, playerLocation.y, playerLocation.z, playerLocation.yaw, playerLocation.pitch))
    }

    override fun sendPlayerToLobby(cloudPlayer: ICloudPlayer): ICommunicationPromise<Unit> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOSendPlayerToLobby(cloudPlayer.getUniqueId()))
    }

    private fun getLocationBySimpleLocation(simpleLocation: SimpleLocation): Location? {
        val world = Bukkit.getWorld(simpleLocation.worldName) ?: return null
        return Location(world, simpleLocation.x, simpleLocation.y, simpleLocation.z, simpleLocation.yaw, simpleLocation.pitch)
    }

    private fun getPlayerByCloudPlayer(cloudPlayer: ICloudPlayer): Player? {
        return Bukkit.getPlayer(cloudPlayer.getUniqueId())
    }
}