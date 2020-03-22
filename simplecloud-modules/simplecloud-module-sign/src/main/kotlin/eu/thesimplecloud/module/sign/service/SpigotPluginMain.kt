package eu.thesimplecloud.module.sign.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.module.sign.lib.SignModuleConfig
import eu.thesimplecloud.module.sign.service.command.CloudSignsCommand
import eu.thesimplecloud.module.sign.service.listener.InteractListener
import org.bukkit.plugin.java.JavaPlugin

class SpigotPluginMain : JavaPlugin() {

    init {
        INSTANCE = this
    }

    lateinit var bukkitCloudSignManager: BukkitCloudSignManager
        private set

    override fun onEnable() {
        getCommand("cloudsigns")?.setExecutor(CloudSignsCommand())
        CloudAPI.instance.getSynchronizedObjectManager()
                .requestSynchronizedObject("simplecloud-module-sign-config", SignModuleConfig::class.java)
                .then { SignModuleConfig.INSTANCE = it }
        this.bukkitCloudSignManager = BukkitCloudSignManager()
        server.pluginManager.registerEvents(InteractListener(), this)
    }

    companion object {
        lateinit var INSTANCE: SpigotPluginMain
    }

}