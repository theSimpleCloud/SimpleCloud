package eu.thesimplecloud.module.proxy.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.module.proxy.config.Config
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.03.2020
 * Time: 21:31
 */
class ProxyModule : ICloudModule{

    val configFile = File("modules/proxy", "config.json")

    override fun onEnable() {
        loadConfig()
        //TODO: create config reload command
    }

    override fun onDisable() {
    }

    fun loadConfig() {
        if (!configFile.exists()) {
            val config = Config.getDefaultConfig()
            saveConfig(config)
            return
        }

        val config = JsonData.fromJsonFile(configFile)!!.getObject(Config::class.java)
        CloudAPI.instance.getSynchronizedObjectManager().updateObject(config)
    }

    fun saveConfig(config: Config) {
        JsonData.fromObject(config).saveAsFile(configFile)
    }

}