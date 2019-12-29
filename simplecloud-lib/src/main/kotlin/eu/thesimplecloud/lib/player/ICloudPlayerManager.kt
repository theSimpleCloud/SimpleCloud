package eu.thesimplecloud.lib.player

import eu.thesimplecloud.lib.exception.NoSuchWorldException
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.location.ServiceLocation
import eu.thesimplecloud.lib.location.SimpleLocation
import eu.thesimplecloud.lib.player.text.CloudText
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.exception.UnavailableServiceException
import eu.thesimplecloud.lib.exception.NoSuchPlayerException
import eu.thesimplecloud.lib.exception.PlayerConnectException
import java.util.*

interface ICloudPlayerManager {

    /**
     * Updates a [ICloudPlayer].
     */
    fun updateCloudPlayer(cloudPlayer: ICloudPlayer)

    /**
     * Removes a [ICloudPlayer] from the cache.
     */
    fun removeCloudPlayer(cloudPlayer: ICloudPlayer)

    /**
     * Returns all cached [ICloudPlayer]s.
     */
    fun getAllCachedCloudPlayers(): List<ICloudPlayer>

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
     * - [UnavailableServiceException] if the proxy service the player is connected to is not reachable
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
     * - [UnavailableServiceException] if the minecraft server the player is connected is not reachable
     * - [NoSuchWorldException] if the world to teleport the player to does not exist or is not loaded.
     */
    fun teleport(cloudPlayer: ICloudPlayer, location: SimpleLocation): ICommunicationPromise<Unit>

    /**
     * Returns the current location of the specified [cloudPlayer]
     * @return a promise that is completed when the [ServiceLocation] is available, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [NoSuchWorldException] if the location of the player was null
     * - [NoSuchPlayerException] if the player cannot be found on the server
     * - [UnavailableServiceException] if the minecraft server the player is connected is not reachable
     */
    fun getLocationOfPlayer(cloudPlayer: ICloudPlayer): ICommunicationPromise<ServiceLocation>

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

}