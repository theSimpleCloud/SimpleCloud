package eu.thesimplecloud.base.manager.player

import java.util.*

interface IOfflinePlayerLoader {

    fun getOfflinePlayer(playerUniqueId: UUID)

}