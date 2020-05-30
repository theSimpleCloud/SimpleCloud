package eu.thesimplecloud.api.event.player

import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.service.ICloudService

class CloudPlayerServerConnectedEvent(cloudPlayer: ICloudPlayer, val server: ICloudService) : CloudPlayerEvent(cloudPlayer)
