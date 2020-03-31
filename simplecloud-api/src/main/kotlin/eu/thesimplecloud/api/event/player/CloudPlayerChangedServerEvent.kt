package eu.thesimplecloud.api.event.player

import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.service.ICloudService

/**
 * This event is called when a player changed his server. The event will only be called on services that receive updates of the player.
 * @see [ICloudPlayer.enableUpdates]
 */
class CloudPlayerChangedServerEvent(
        /**
         * The player involved in this event.
         */
        val cloudPlayer: ICloudPlayer,
        /**
         * The server the player switched from. The server can be null if the player was on no server before.
         */
        val from: ICloudService?,
        /**
         * The server the player switched to.
         */
        val to: ICloudService
) : IEvent