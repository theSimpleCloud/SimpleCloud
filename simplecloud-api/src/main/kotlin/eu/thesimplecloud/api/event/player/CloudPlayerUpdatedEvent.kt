package eu.thesimplecloud.api.event.player

import eu.thesimplecloud.api.player.ICloudPlayer

/**
 * This event is called when a network component receives a update of a player. Note, that not every component will receive updates.
 * To read more about updates
 * @see ICloudPlayer.enableUpdates
 */
class CloudPlayerUpdatedEvent(cloudPlayer: ICloudPlayer) : CloudPlayerEvent(cloudPlayer)