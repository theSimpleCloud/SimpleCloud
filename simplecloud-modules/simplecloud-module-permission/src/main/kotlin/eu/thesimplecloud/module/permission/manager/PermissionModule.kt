package eu.thesimplecloud.module.permission.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.player.OfflineCloudPlayer
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.group.PermissionGroup
import eu.thesimplecloud.module.permission.group.manager.PermissionGroupManager
import eu.thesimplecloud.module.permission.manager.command.PermissionCommand
import eu.thesimplecloud.module.permission.permission.Permission
import eu.thesimplecloud.module.permission.player.IPermissionPlayer
import eu.thesimplecloud.module.permission.player.PermissionPlayer
import java.io.File

class PermissionModule : ICloudModule {
    //TODO: mehrere gruppen mit ablaufzeit
    companion object {
        val GROUPS_FILE = File("modules/permissions/groups.json")
        @JvmStatic
        lateinit var instance: PermissionModule
            private set
    }

    init {
        instance = this
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

    fun updatePermissionPlayer(permissionPlayer: IPermissionPlayer) {
        val offlineCloudPlayerHandler = Manager.instance.offlineCloudPlayerHandler
        val cloudPlayer = permissionPlayer.getCloudPlayer().getNow()
        if (cloudPlayer != null) {
            permissionPlayer.update()
            offlineCloudPlayerHandler.saveCloudPlayer(cloudPlayer.toOfflinePlayer() as OfflineCloudPlayer)
            cloudPlayer.sendMessage("§cYou were updated.")
        } else {
            val offlinePlayer = offlineCloudPlayerHandler.getOfflinePlayer(permissionPlayer.getName()) ?: return
            offlinePlayer.setProperty(PermissionPlayer.PROPERTY_NAME, Property(permissionPlayer))
            offlineCloudPlayerHandler.saveCloudPlayer(offlinePlayer as OfflineCloudPlayer)
        }
    }
}