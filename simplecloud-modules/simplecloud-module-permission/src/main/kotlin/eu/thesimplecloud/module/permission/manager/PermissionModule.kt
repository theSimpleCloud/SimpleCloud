package eu.thesimplecloud.module.permission.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.group.PermissionGroup
import eu.thesimplecloud.module.permission.group.manager.PermissionGroupManager
import eu.thesimplecloud.module.permission.permission.Permission
import java.io.File
import java.lang.IllegalStateException

class PermissionModule : ICloudModule {
    //TODO: mehrere gruppen mit ablaufzeit
    private val groupsFile = File("modules/permissions/groups.json")

    override fun onEnable() {
        if (!groupsFile.exists()) {
            val permissionGroupManager = PermissionGroupManager()
            val adminGroup = PermissionGroup("Admin")
            adminGroup.addPermission(Permission("*", -1, true))
            val defaultGroup = PermissionGroup("default")
            permissionGroupManager.update(adminGroup)
            permissionGroupManager.update(defaultGroup)
            JsonData.fromObject(permissionGroupManager).saveAsFile(groupsFile)
        }
        val permissionGroupManager = JsonData.fromJsonFile(groupsFile)?.getObject(PermissionGroupManager::class.java)
                ?: throw IllegalStateException("PermissionGroupManager is null")
        PermissionPool(permissionGroupManager)
        CloudAPI.instance.getEventManager().registerListener(this, CloudListener())
    }

    override fun onDisable() {
    }
}