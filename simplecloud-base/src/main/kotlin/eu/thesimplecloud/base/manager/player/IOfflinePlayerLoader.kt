package eu.thesimplecloud.base.manager.player

import eu.thesimplecloud.lib.player.IOfflineCloudPlayer
import java.util.*

interface IOfflinePlayerLoader {

    fun getOfflinePlayer(playerUniqueId: UUID): IOfflineCloudPlayer?

}