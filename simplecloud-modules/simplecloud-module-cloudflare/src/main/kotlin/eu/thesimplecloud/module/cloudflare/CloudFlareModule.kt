package eu.thesimplecloud.module.cloudflare

import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.module.cloudflare.config.ConfigManager

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.02.2020
 * Time: 17:31
 */
class CloudFlareModule: ICloudModule {

    val configManager = ConfigManager()
    val config = configManager.getConfig()

    override fun onEnable() {

    }

    override fun onDisable() {
    }
}