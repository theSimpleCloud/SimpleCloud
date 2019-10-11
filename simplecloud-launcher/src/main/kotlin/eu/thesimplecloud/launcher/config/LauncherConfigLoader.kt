package eu.thesimplecloud.launcher.config

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.config.IConfigLoader
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import java.io.File
import java.lang.IllegalStateException

class LauncherConfigLoader : IConfigLoader<LauncherConfig> {


    val file = File("launcher.json")

    override fun loadConfig(): LauncherConfig {
        if (!file.exists()) return LauncherConfig("127.0.0.1", 1900, DirectoryPaths())
        return JsonData.fromJsonFile(file)?.getObjectOrNull(LauncherConfig::class.java) ?: throw IllegalStateException("Error while loading manager config: Couldn't parse file.")
    }

    override fun saveConfig(value: LauncherConfig) {
        JsonData.fromObject(value).saveAsFile(file)
    }

    override fun doesConfigFileExist(): Boolean = file.exists()

}