package eu.thesimplecloud.module.permission

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.module.permission.group.manager.IPermissionGroupManager
import eu.thesimplecloud.module.permission.group.manager.PermissionGroupManager
import eu.thesimplecloud.module.permission.manager.PermissionModule
import eu.thesimplecloud.module.permission.player.manager.IPermissionPlayerManager
import eu.thesimplecloud.plugin.startup.CloudPlugin

class PermissionPool(private val permissionGroupManager: PermissionGroupManager) : IPermissionPool {

    private val permissionPlayerManager = object : IPermissionPlayerManager {}

    init {
        instance = this
        CloudAPI.instance.getSynchronizedObjectListManager().registerSynchronizedObjectList(permissionGroupManager)
        val cloudModule = if (CloudAPI.instance.isManager()) {
            PermissionModule.instance
        } else {
            CloudPlugin.instance
        }
        CloudAPI.instance.getEventManager().registerListener(cloudModule, PermissionCheckListener())
    }


    override fun getPermissionGroupManager(): IPermissionGroupManager = this.permissionGroupManager

    override fun getPermissionPlayerManager(): IPermissionPlayerManager = this.permissionPlayerManager

    companion object {
        @JvmStatic
        lateinit var instance: PermissionPool
            private set
    }

}