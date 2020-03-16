package eu.thesimplecloud.module.permission

import eu.thesimplecloud.module.permission.group.manager.IPermissionGroupManager
import eu.thesimplecloud.module.permission.player.manager.IPermissionPlayerManager

interface IPermissionPool {

    /**
     * Returns the [IPermissionGroupManager]
     */
    fun getPermissionGroupManager(): IPermissionGroupManager

    /**
     * Returns the [IPermissionPlayerManager]
     */
    fun getPermissionPlayerManager() : IPermissionPlayerManager

}