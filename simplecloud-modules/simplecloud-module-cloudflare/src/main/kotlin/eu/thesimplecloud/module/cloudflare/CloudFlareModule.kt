package eu.thesimplecloud.module.cloudflare

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.module.cloudflare.api.CloudFlareAPI
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
    val cloudFlareAPI = CloudFlareAPI(config)

    override fun onEnable() {
        CloudAPI.instance.getEventManager().registerListener(this, CloudListener(this))

        cloudFlareAPI.createForAlreadyStartedServices()
    }

    override fun onDisable() {
    }
}