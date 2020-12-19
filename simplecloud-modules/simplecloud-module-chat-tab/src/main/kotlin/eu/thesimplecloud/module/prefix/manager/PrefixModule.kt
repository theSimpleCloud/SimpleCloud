package eu.thesimplecloud.module.prefix.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.module.prefix.config.ConfigLoader

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 19.12.2020
 * Time: 12:51
 */
class PrefixModule : ICloudModule {

    override fun onEnable() {
        val configLoader = ConfigLoader()
        val config = configLoader.loadConfig()
        CloudAPI.instance.getGlobalPropertyHolder().setProperty("prefix-config", config)
    }

    override fun onDisable() {
    }

}