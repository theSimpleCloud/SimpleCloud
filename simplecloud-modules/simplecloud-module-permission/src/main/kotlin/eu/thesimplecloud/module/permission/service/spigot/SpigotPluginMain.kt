package eu.thesimplecloud.module.permission.service.spigot


import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.group.manager.PermissionGroupManager
import org.bukkit.plugin.java.JavaPlugin

class SpigotPluginMain : JavaPlugin() {


    override fun onEnable() {
        PermissionPool(PermissionGroupManager())
        this.server.pluginManager.registerEvents(SpigotListener(), this)
    }

}