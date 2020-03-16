package eu.thesimplecloud.module.permission

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.module.permission.group.manager.IPermissionGroupManager
import eu.thesimplecloud.module.permission.group.manager.PermissionGroupManager
import eu.thesimplecloud.module.permission.player.manager.IPermissionPlayerManager

class PermissionPool(private val permissionGroupManager: PermissionGroupManager) : IPermissionPool {

    private val permissionPlayerManager = object : IPermissionPlayerManager {}

    init {
        instance = this
        CloudAPI.instance.getSynchronizedObjectListManager().registerSynchronizedObjectList(permissionGroupManager)
    }



    override fun getPermissionGroupManager(): IPermissionGroupManager = this.permissionGroupManager

    override fun getPermissionPlayerManager(): IPermissionPlayerManager = this.permissionPlayerManager

    companion object {
        lateinit var instance: PermissionPool
    }

}