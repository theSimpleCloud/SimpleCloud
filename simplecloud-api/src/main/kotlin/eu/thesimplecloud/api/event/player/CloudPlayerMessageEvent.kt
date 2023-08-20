package eu.thesimplecloud.api.event.player

import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.service.ICloudService

/**
 * Created by MrManHD
 * Class create at 23.06.2023 23:09
 */

/**
 * This event is called when a player sends a chat message
 */
class CloudPlayerMessageEvent(
    cloudPlayer: ICloudPlayer,

    /**
     * The chat message from this player
     */
    val message: String,

    /**
     * The cloud service where the player sent the chat message
     */
    val cloudService: ICloudService
) : CloudPlayerEvent(cloudPlayer)