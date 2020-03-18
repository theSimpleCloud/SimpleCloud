package eu.thesimplecloud.api.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.exception.NoSuchWorldException
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.location.ServiceLocation
import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.exception.UnreachableServiceException
import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.api.exception.PlayerConnectException
import eu.thesimplecloud.api.exception.NoSuchServiceException
import eu.thesimplecloud.api.executeOnManager
import java.util.*

interface ICloudPlayerManager {

    /**
     * Updates a [ICloudPlayer].
     */
    fun updateCloudPlayer(cloudPlayer: ICloudPlayer, fromPacket: Boolean = false)

    /**
     * Removes a [ICloudPlayer] from the cache.
     */
    fun removeCloudPlayer(cloudPlayer: ICloudPlayer)

    /**
     * Returns all cached [ICloudPlayer]s.
     */
    fun getAllCachedCloudPlayers(): Collection<ICloudPlayer>

    /**
     * Returns the cached [ICloudPlayer] found by the specified [uniqueId]
     */
    fun getCachedCloudPlayer(uniqueId: UUID): ICloudPlayer? = getAllCachedCloudPlayers().firstOrNull { it.getUniqueId() == uniqueId }

    /**
     * Returns the cached [ICloudPlayer] found by the specified [name]
     */
    fun getCachedCloudPlayer(name: String): ICloudPlayer? = getAllCachedCloudPlayers().firstOrNull { it.getName().equals(name, true) }

    /**
     * Returns a promise that will be completed with the requested [ICloudPlayer]
     * If the player can not be found the promise will fail with [NoSuchElementException]
     */
    fun getCloudPlayer(uniqueId: UUID): ICommunicationPromise<ICloudPlayer>

    /**
     * Returns a promise that will be completed with the requested [ICloudPlayer]
     * If the player can not be found the promise will fail with [NoSuchElementException]
     */
    fun getCloudPlayer(name: String): ICommunicationPromise<ICloudPlayer>

    /**
     * Sends a message to a player.
     * @param cloudPlayer the player that shall receive the message
     * @param cloudText the text to send.
     */
    fun sendMessageToPlayer(cloudPlayer: ICloudPlayer, cloudText: CloudText)

    /**
     * Sends the [cloudPlayer] to the specified [cloudService]
     * @param cloudPlayer the [ICloudPlayer] to send to the specified service.
     * @param cloudService the service the player shall be sent to.
     * @return a promise that is completed when the connection is complete, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [UnreachableServiceException] if the proxy service the player is connected to is not reachable
     * - [IllegalArgumentException] if the specified [cloudService] is a proxy service.
     * - [NoSuchPlayerException] if the player cannot be found on the proxy service.
     * - [PlayerConnectException] if the proxy was unable to connect the player to the service.
     */
    fun connectPlayer(cloudPlayer: ICloudPlayer, cloudService: ICloudService): ICommunicationPromise<Unit>

    /**
     * Kicks the specified player with the specified message from the network.
     * @param cloudPlayer the [ICloudPlayer] that shall be kicked.
     * @param message the message with the player shall be kicked.
     */
    fun kickPlayer(cloudPlayer: ICloudPlayer, message: String)

    /**
     * Sends a tile to the specified player.
     * @param cloudPlayer the player that shall received the title.
     * @param title the title that shall be send to the player.
     * @param subTitle the subtitle that shall be send to the player.
     * @param fadeIn the amount of ticks the title shall fade in.
     * @param stay the amount of ticks the title shall stay.
     * @param fadeOut the amount of ticks the title shall fade out.
     */
    fun sendTitle(cloudPlayer: ICloudPlayer, title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int)

    /**
     * Lets the specified [cloudPlayer] executes the specified [command]
     */
    fun forcePlayerCommandExecution(cloudPlayer: ICloudPlayer, command: String)

    /**
     * Sends a action bar to the specified player.
     * @param cloudPlayer the [ICloudPlayer] that shall receive the [actionbar]
     * @param actionbar the actionbar content
     */
    fun sendActionbar(cloudPlayer: ICloudPlayer, actionbar: String)

    /**
     * Tells the manager that this instance wants to receive updates of the specified [cloudPlayer]
     * @param cloudPlayer the player
     * @param update whether updates shall be sent.
     */
    fun setUpdates(cloudPlayer: ICloudPlayer, update: Boolean, serviceName: String)

    /**
     * Teleports the specified [cloudPlayer] to the specified [location].
     * @return a promise that is completed when the teleportation is complete, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [UnreachableServiceException] if the minecraft server the player is connected is not reachable.
     * - [NoSuchWorldException] if the world to teleport the player to does not exist or is not loaded.
     * - [IllegalStateException] if the player is not connected to a server.
     */
    fun teleportPlayer(cloudPlayer: ICloudPlayer, location: SimpleLocation): ICommunicationPromise<Unit>

    /**
     * Teleports the specified [cloudPlayer] to the specified [location].
     * @return a promise that is completed when the teleportation is complete, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [UnreachableServiceException] if the minecraft server the player is connected is not reachable.
     * - [NoSuchServiceException] if the service to connect the player to cannot be found.
     * - [NoSuchWorldException] if the world to teleport the player to does not exist or is not loaded.
     * - [IllegalStateException] if the player is not connected to a server.
     * - [IllegalArgumentException] if the service in [ServiceLocation.getService] is a proxy service.
     * - [NoSuchPlayerException] if the player cannot be found on the proxy or the server service.
     * - [PlayerConnectException] if the proxy was unable to connect the player to the service.
     */
    fun teleportPlayer(cloudPlayer: ICloudPlayer, location: ServiceLocation): ICommunicationPromise<Unit>

    /**
     * Checks whether the specified [cloudPlayer] has the specified [permission]
     * @return a promise that is completed when the permission is checked, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [UnreachableServiceException] if the proxy server the player is connected is not reachable.
     * - [NoSuchPlayerException] if the player cannot be found on the proxy.
     */
    fun hasPermission(cloudPlayer: ICloudPlayer, permission: String): ICommunicationPromise<Boolean>

    /**
     * Returns the current location of the specified [cloudPlayer]
     * @return a promise that is completed when the [ServiceLocation] is available, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [NoSuchWorldException] if the location of the player was null.
     * - [NoSuchPlayerException] if the player cannot be found on the server.
     * - [UnreachableServiceException] if the minecraft server the player is connected is not reachable.
     */
    fun getLocationOfPlayer(cloudPlayer: ICloudPlayer): ICommunicationPromise<ServiceLocation>

    /**
     * Sends the specified [cloudPlayer] to a lobby server
     * @return a promise that is completed when the [cloudPlayer] is connected to the lobby server, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [NoSuchPlayerException] if the player cannot be found on the proxy.
     * - [UnreachableServiceException] if the proxy server the player is connected is not reachable.
     * - [NoSuchServiceException] if no lobby was available to send the player to.
     */
    fun sendPlayerToLobby(cloudPlayer: ICloudPlayer): ICommunicationPromise<Unit>

    /**
     * Returns the current location of this player
     * @return a promise that is completed when the [IOfflineCloudPlayer] is available, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [NoSuchPlayerException] if the [IOfflineCloudPlayer] cannot be found by the specified [name]
     */
    fun getOfflineCloudPlayer(name: String): ICommunicationPromise<IOfflineCloudPlayer>


    /**
     * Returns the current location of this player
     * @return a promise that is completed when the [IOfflineCloudPlayer] is available, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [NoSuchPlayerException] if the [IOfflineCloudPlayer] cannot be found by the specified [uniqueId]
     */
    fun getOfflineCloudPlayer(uniqueId: UUID): ICommunicationPromise<IOfflineCloudPlayer>

    /**
     * Returns the current online count
     */
    fun getOnlinePlayerCount(): ICommunicationPromise<Int> = CloudAPI.instance.executeOnManager { CloudAPI.instance.getCloudPlayerManager().getAllCachedCloudPlayers().size }

}