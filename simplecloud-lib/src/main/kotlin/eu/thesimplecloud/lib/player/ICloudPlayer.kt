package eu.thesimplecloud.lib.player

import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.flatten
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.exception.NoSuchPlayerException
import eu.thesimplecloud.lib.exception.NoSuchWorldException
import eu.thesimplecloud.lib.exception.UnavailableServiceException
import eu.thesimplecloud.lib.location.ServiceLocation
import eu.thesimplecloud.lib.location.SimpleLocation
import eu.thesimplecloud.lib.player.connection.IPlayerConnection
import eu.thesimplecloud.lib.player.text.CloudText
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceType
import java.net.InetAddress

interface ICloudPlayer : IOfflineCloudPlayer {

    /**
     * Returns the [IPlayerConnection] of this player.
     */
    fun getPlayerConnection(): IPlayerConnection

    /**
     * Sends a message to this player.
     */
    fun sendMessage(cloudText: CloudText) = CloudLib.instance.getCloudPlayerManager().sendMessageToPlayer(this, cloudText)

    /**
     * Sends a message to this player.
     */
    fun sendMessage(message: String) = sendMessage(CloudText(message))

    /**
     * Sends this player to the specified [cloudService]
     * @param cloudService the service the player shall be sent to.
     * @return a promise that is completed when the connection is complete, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [UnavailableServiceException] if the proxy service the player is connected to is not reachable
     * - [IllegalArgumentException] if the specified [cloudService] is a proxy service.
     */
    fun connect(cloudService: ICloudService): ICommunicationPromise<Unit> = CloudLib.instance.getCloudPlayerManager().connectPlayer(this, cloudService)

    /**
     * Kicks this player form the network.
     */
    fun kick(message: String) = CloudLib.instance.getCloudPlayerManager().kickPlayer(this, message)

    /**
     * Kicks this player from the network.
     */
    fun kick() = kick("§cYou were kicked from the network.")

    /**
     * Sends a title to this player.
     */
    fun sendTitle(title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) = CloudLib.instance.getCloudPlayerManager().sendTitle(this, title, subTitle, fadeIn, stay, fadeOut)

    /**
     * Sends a action bar to this player
     */
    fun sendActionBar(actionbar: String) = CloudLib.instance.getCloudPlayerManager().sendActionbar(this, actionbar)

    /**
     * Returns the name of the proxy the player is connected to.
     */
    fun getConnectedProxyName(): String

    /**
     * Returns the name of the server the player is connected to.
     */
    fun getConnectedServerName(): String?

    /**
     * Returns the proxy this player is connected to.
     */
    fun getConnectedProxy(): ICloudService? = CloudLib.instance.getCloudServiceManger().getCloudService(getConnectedProxyName())

    /**
     * Returns the server this player is connected to.
     */
    fun getConnectedServer(): ICloudService? = getConnectedServerName()?.let { CloudLib.instance.getCloudServiceManger().getCloudService(it) }

    /**
     * Tells the manager that this client wants to receive updates of this player.
     */
    fun enableUpdates() = CloudLib.instance.getCloudPlayerManager().setUpdates(this, true, CloudLib.instance.getThisSidesName())

    /**
     * Tells the manager that this client no longer wants to receive updates of this player.
     */
    fun disableUpdates() = CloudLib.instance.getCloudPlayerManager().setUpdates(this, false, CloudLib.instance.getThisSidesName())

    /**
     * Lets this player executes the specified [command]
     */
    fun forceCommandExecution(command: String) = CloudLib.instance.getCloudPlayerManager().forcePlayerCommandExecution(this, command)

    /**
     * Teleports this player to the specified [location].
     * @return a promise that is completed when the teleportation is complete, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [UnavailableServiceException] if the minecraft server the player is connected is not reachable
     * - [NoSuchWorldException] if the world to teleport the player to does not exist or is not loaded.
     */
    fun teleport(location: SimpleLocation): ICommunicationPromise<Unit> = CloudLib.instance.getCloudPlayerManager().teleport(this, location)

    /**
     * Teleports this player to the specified [location]
     * If the player is not connected to the service specified in the [location] he will be sent to the service.
     * @return a promise that is completed when the teleportation is complete, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [UnavailableServiceException] if the proxy service or the minecraft server the player is connected to is not reachable
     * - [IllegalArgumentException] if [ServiceLocation.getService] is a proxy service.
     * - [NoSuchWorldException] if the world to teleport the player to does not exist or is not loaded.
     */
    fun teleport(location: ServiceLocation): ICommunicationPromise<Unit> {
        val locationService = location.getService()
                ?: return CommunicationPromise.failed(UnavailableServiceException("Service not found"))
        return this.connect(locationService).then { this.teleport(location) }.flatten().addFailureListener { this.sendMessage("§cTeleportation failed.") }
    }

    /**
     *
     * @return a promise that is completed when the [ServiceLocation] is available, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [UnavailableServiceException] if the player is not connected to a server or the server is not connected to the manager.
     */
    fun getLocation(): ICommunicationPromise<ServiceLocation> = CloudLib.instance.getCloudPlayerManager().getLocationOfPlayer(this)

}