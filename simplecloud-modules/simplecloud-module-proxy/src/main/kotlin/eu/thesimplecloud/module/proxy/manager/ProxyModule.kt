package eu.thesimplecloud.module.proxy.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.proxy.config.Config
import eu.thesimplecloud.module.proxy.config.DefaultConfig
import eu.thesimplecloud.module.proxy.config.ProxyGroupConfiguration
import eu.thesimplecloud.module.proxy.manager.commands.ProxyCommand
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.03.2020
 * Time: 21:31
 */
class ProxyModule : ICloudModule{

    val configFile = File("modules/proxy", "config.json")
    lateinit var config: Config

    override fun onEnable() {
        loadConfig()
        Launcher.instance.commandManager
                .registerCommand(this, ProxyCommand(this))
    }

    override fun onDisable() {
    }

    fun loadConfig() {
        if (!configFile.exists()) {
            val config = DefaultConfig.get()
            saveConfig(config)
            this.config = config
        }

        val config = JsonData.fromJsonFile(configFile)!!.getObject(Config::class.java)
        CloudAPI.instance.getSingleSynchronizedObjectManager().updateObject(config)
        this.config = config
    }

    fun saveConfig(config: Config) {
        JsonData.fromObject(config).saveAsFile(configFile)
    }

    fun saveConfig() {
        saveConfig(config)
    }

    fun getProxyConfiguration(serviceGroupName: String): ProxyGroupConfiguration? {
        return config.proxyGroupConfigurations.firstOrNull { it.proxyGroup == serviceGroupName }
    }

}