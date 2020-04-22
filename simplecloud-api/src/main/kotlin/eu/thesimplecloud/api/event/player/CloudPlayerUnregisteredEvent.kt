package eu.thesimplecloud.api.event.player

import eu.thesimplecloud.api.player.ICloudPlayer

/**
 * This event is called when a player was removed from this network component. This event is not called when a player leaves
 * the network component.
 * @see CloudPlayerDisconnectEvent
 */
class CloudPlayerUnregisteredEvent(cloudPlayer: ICloudPlayer) : CloudPlayerEvent(cloudPlayer)