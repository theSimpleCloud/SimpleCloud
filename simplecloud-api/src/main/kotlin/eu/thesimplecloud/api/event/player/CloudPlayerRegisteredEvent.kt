package eu.thesimplecloud.api.event.player

import eu.thesimplecloud.api.player.ICloudPlayer

/**
 * This event is called when a player is updated the first time on a service. This event means not that the player is now logged in to the network.
 * @see CloudPlayerLoginEvent
 */
class CloudPlayerRegisteredEvent(cloudPlayer: ICloudPlayer) : CloudPlayerEvent(cloudPlayer)