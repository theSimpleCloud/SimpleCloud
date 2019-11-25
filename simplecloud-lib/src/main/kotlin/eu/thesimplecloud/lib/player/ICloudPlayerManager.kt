package eu.thesimplecloud.lib.player

import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.lib.player.text.CloudText
import eu.thesimplecloud.lib.service.ICloudService
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
     */
    fun getCloudPlayer(uniqueId: UUID): ICommunicationPromise<ICloudPlayer>

    /**
     * Returns a promise that will be completed with the requested [ICloudPlayer]
     */
    fun getCloudPlayer(name: String): ICommunicationPromise<ICloudPlayer>

    /**
     * Sends a message to a player.
     * @param cloudPlayer the player that shall receive the message
     * @param cloudText the text to send.
     */
    fun sendMessageToPlayer(cloudPlayer: ICloudPlayer, cloudText: CloudText)

    /**
     * Sends this player to the specified [cloudService]
     * @param cloudPlayer the [ICloudPlayer] to send to the specified service.
     * @param cloudService the service the player shall be sent to.
     * @throws IllegalArgumentException when the specified service is a proxy service.
     * @return a promise that will be completed when then player is connected to the specified service.
     */
    fun sendPlayerToService(cloudPlayer: ICloudPlayer, cloudService: ICloudService): ICommunicationPromise<Unit>

    /**
     * Kicks the specified player with the specified message from the network.
     * @param cloudPlayer the [ICloudPlayer] that shall be kicked.
     * @param message the message with the player shall be kicked.
     */
    fun kickPlayer(cloudPlayer: ICloudPlayer, message: String)

    /**
     * Sends a tile to the specified player.
     * @param cloudPlayer the player that shall received the title
     * @param title the title that shall be send to the player.
     * @param subTitle the subtitle that shall be send to the player.
     * @param fadeIn the amount of ticks the title shall fade in.
     * @param stay the amount of ticks the title shall stay.
     * @param fadeOut the amount of ticks the title shall fade out.
     */
    fun sendTitle(cloudPlayer: ICloudPlayer, title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int)

}