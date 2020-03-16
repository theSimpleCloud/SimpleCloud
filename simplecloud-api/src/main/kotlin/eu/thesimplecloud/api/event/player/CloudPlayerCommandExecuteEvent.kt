package eu.thesimplecloud.api.event.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.eventapi.ISynchronizedEvent
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

/**
 * This event will be called when a player executes any command
 */
class CloudPlayerCommandExecuteEvent(
        /**
         * The unique id of the player involved in this event
         */
        val playerUniqueId: UUID,

        /**
         * The name of the player involved in this event
         */
        val playerName: String,

        /**
         * The executed command
         */
        val command: String
) : ISynchronizedEvent {

    fun getCloudPlayer(): ICommunicationPromise<ICloudPlayer> = CloudAPI.instance.getCloudPlayerManager().getCloudPlayer(playerUniqueId)
}