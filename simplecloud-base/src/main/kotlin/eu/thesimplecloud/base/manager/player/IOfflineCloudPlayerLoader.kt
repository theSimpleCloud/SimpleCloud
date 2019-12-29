package eu.thesimplecloud.base.manager.player

import eu.thesimplecloud.lib.player.IOfflineCloudPlayer
import java.util.*

interface IOfflineCloudPlayerLoader {

    /**
     * Returns the [IOfflineCloudPlayer] found by the specified [playerUniqueId]
     */
    fun getOfflinePlayer(playerUniqueId: UUID): IOfflineCloudPlayer?

    /**
     * Returns the [IOfflineCloudPlayer] found by the specified [name]
     */
    fun getOfflinePlayer(name: String): IOfflineCloudPlayer?

}