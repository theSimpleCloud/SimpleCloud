package eu.thesimplecloud.base.manager.player

import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.OfflineCloudPlayer
import java.util.*

interface IOfflineCloudPlayerHandler {

    /**
     * Returns the [IOfflineCloudPlayer] found by the specified [playerUniqueId]
     */
    fun getOfflinePlayer(playerUniqueId: UUID): IOfflineCloudPlayer?

    /**
     * Returns the [IOfflineCloudPlayer] found by the specified [name]
     */
    fun getOfflinePlayer(name: String): IOfflineCloudPlayer?

    /**
     * Saves the specified [offlineCloudPlayer] to the database.
     */
    fun saveCloudPlayer(offlineCloudPlayer: OfflineCloudPlayer)

}