package eu.thesimplecloud.api.event.player

import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.player.ICloudPlayer

open class CloudPlayerEvent(val cloudPlayer: ICloudPlayer) : IEvent