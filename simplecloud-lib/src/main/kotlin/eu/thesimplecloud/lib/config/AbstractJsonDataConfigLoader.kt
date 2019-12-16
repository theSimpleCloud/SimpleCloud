package eu.thesimplecloud.lib.config

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import java.io.File

abstract class AbstractJsonDataConfigLoader<T : Any>(val configClass: Class<T>, val configFie: File, val lazyDefaultObject: () -> T) : IConfigLoader<T> {

    override fun loadConfig(): T {
        return JsonData.fromJsonFile(configFie)?.getObjectOrNull(configClass) ?: lazyDefaultObject()
    }

    override fun saveConfig(value: T) {
        JsonData.fromObject(value).saveAsFile(configFie)
    }

    override fun doesConfigFileExist(): Boolean = this.configFie.exists()


}