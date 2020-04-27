package eu.thesimplecloud.module.notify

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.notify.config.Config
import eu.thesimplecloud.module.notify.config.DefaultConfig
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 13.04.2020
 * Time: 16:26
 */
class NotifyModule : ICloudModule {

    val configFile = File("modules/notify", "config.json")
    lateinit var config: Config

    override fun onEnable() {
        loadConfig()
        CloudAPI.instance.getEventManager().registerListener(this, CloudListener(this))
        //Launcher.instance.commandManager
                //.registerCommand(this, NotifyCommand(this))
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
        this.config = config
    }

    fun saveConfig(config: Config) {
        JsonData.fromObject(config).saveAsFile(configFile)
    }

    fun saveConfig() {
        saveConfig(config)
    }

}