package eu.thesimplecloud.module.proxy.manager.converter

import java.io.File

/**
 * Date: 19.06.22
 * Time: 12:59
 * @author Frederick Baier
 *
 */
class ConfigConverter {

    private val configFile = File("modules/proxy", "config.json")

    fun convertIfNecessary() {
        if (!configFile.exists())
            return
        MiniMessageConfigConverter().convertIfNecessary()
    }

}