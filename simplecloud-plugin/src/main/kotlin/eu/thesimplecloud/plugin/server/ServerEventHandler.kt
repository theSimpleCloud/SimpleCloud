package eu.thesimplecloud.plugin.server

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.plugin.startup.CloudPlugin
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 17.01.2021
 * Time: 20:59
 */
object ServerEventHandler {

    fun onPlayerDisconnected(uniqueId: UUID, onlinePlayers: Int) {
        val playerManager = CloudAPI.instance.getCloudPlayerManager()
        val cloudPlayer = playerManager.getCachedCloudPlayer(uniqueId)

        if (cloudPlayer != null && !cloudPlayer.isUpdatesEnabled()) {
            playerManager.delete(cloudPlayer)
        }
        updateCurrentOnlineCountTo(onlinePlayers)
    }

    fun updateCurrentOnlineCountTo(count: Int) {
        val thisService = CloudPlugin.instance.thisService()
        thisService.setOnlineCount(count)
        thisService.update()
    }

}