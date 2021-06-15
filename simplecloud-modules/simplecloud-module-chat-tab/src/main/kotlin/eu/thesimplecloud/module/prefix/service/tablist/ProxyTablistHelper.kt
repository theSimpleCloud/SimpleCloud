package eu.thesimplecloud.module.prefix.service.tablist

import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.prefix.config.Config
import eu.thesimplecloud.module.prefix.config.TablistInformation
import java.util.*

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 */
object ProxyTablistHelper {

    fun getTablistInformationByUUID(uuid: UUID): TablistInformation? {
        val permissionPlayer = PermissionPool.instance.getPermissionPlayerManager().getCachedPermissionPlayer(uuid) ?: return null

        val informationList = Config.getConfig().informationList
        val tablistInformation = informationList.sortedBy { it.priority }.first {
            permissionPlayer.hasPermissionGroup(it.groupName)
        }

        return tablistInformation
    }

}