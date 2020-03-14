package eu.thesimplecloud.module.cloudflare.config

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.module.cloudflare.CloudFlareModule
import java.io.File
import kotlin.contracts.contract

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.02.2020
 * Time: 17:40
 */
class ConfigManager() {

    fun createDefaultConfig(): Config {
        val file = File("CloudFlare", "config.json")
        if (!file.exists()) {
            val config = Config("user@thesimplecloud.eu", "", "thesimplecloud.eu", "", "@")
            JsonData().append("data", config).saveAsFile(file)
            return config
        }

        return getConfig()
    }

    fun getConfig(): Config {
        val file = File("CloudFlare", "config.json")
        val jsonData = JsonData.fromJsonFile(file)
        val config = if (jsonData == null) createDefaultConfig()
                    else jsonData.getObject("data", Config::class.java)!!

        return config
    }

}