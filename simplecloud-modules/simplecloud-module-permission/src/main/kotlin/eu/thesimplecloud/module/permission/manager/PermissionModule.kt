package eu.thesimplecloud.module.permission.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.group.PermissionGroup
import eu.thesimplecloud.module.permission.group.manager.PermissionGroupManager
import eu.thesimplecloud.module.permission.manager.command.PermissionCommand
import eu.thesimplecloud.module.permission.permission.Permission
import java.io.File
import java.lang.IllegalStateException

class PermissionModule : ICloudModule {
    //TODO: mehrere gruppen mit ablaufzeit
    companion object {
        val GROUPS_FILE = File("modules/permissions/groups.json")
    }

    override fun onEnable() {
        if (!GROUPS_FILE.exists()) {
            val permissionGroupManager = PermissionGroupManager()
            val adminGroup = PermissionGroup("Admin")
            adminGroup.addPermission(Permission("*", -1, true))
            val defaultGroup = PermissionGroup("default")
            permissionGroupManager.update(adminGroup)
            permissionGroupManager.update(defaultGroup)
            JsonData.fromObject(permissionGroupManager).saveAsFile(GROUPS_FILE)
        }
        val permissionGroupManager = JsonData.fromJsonFile(GROUPS_FILE)?.getObject(PermissionGroupManager::class.java)
                ?: throw IllegalStateException("PermissionGroupManager is null")
        PermissionPool(permissionGroupManager)
        CloudAPI.instance.getEventManager().registerListener(this, CloudListener())
        Launcher.instance.commandManager.registerCommand(this, PermissionCommand())
    }

    override fun onDisable() {
    }
}