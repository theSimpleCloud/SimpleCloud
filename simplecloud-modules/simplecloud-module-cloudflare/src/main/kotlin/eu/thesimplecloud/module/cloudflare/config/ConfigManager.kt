package eu.thesimplecloud.module.cloudflare.config

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.02.2020
 * Time: 17:40
 */
class ConfigManager() {

    private fun createDefaultConfig(): Config {
        val file = File("CloudFlare", "config.json")
        if (!file.exists()) {
            val config = Config(listOf(CloudFlareData("proxy", "user@thesimplecloud.eu", "", "thesimplecloud.eu", "", "@")))
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