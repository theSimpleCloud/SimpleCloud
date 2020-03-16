package eu.thesimplecloud.module.permission.service.bungee

import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.group.manager.PermissionGroupManager
import net.md_5.bungee.api.plugin.Plugin

class BungeePluginMain : Plugin() {

    override fun onEnable() {
        PermissionPool(PermissionGroupManager())
        this.proxy.pluginManager.registerListener(this, BungeeListener())
    }

}