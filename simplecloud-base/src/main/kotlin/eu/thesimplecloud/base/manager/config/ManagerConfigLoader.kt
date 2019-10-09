package eu.thesimplecloud.base.manager.config

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.config.IConfigLoader
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import java.io.File
import java.lang.IllegalStateException

class ManagerConfigLoader : IConfigLoader<ManagerConfig> {

    private val file = File(DirectoryPaths.paths.configurationsPath, "manager.json")

    override fun loadConfig(): ManagerConfig {
        if (!file.exists()) return ManagerConfig(mutableListOf())
        return JsonData.fromJsonFile(file).getObject(ManagerConfig::class.java) ?: throw IllegalStateException("Error while loading manager config: Couldn't parse file.")
    }

    override fun saveConfig(managerConfig: ManagerConfig) {
        JsonData.fromObject(managerConfig).saveAsFile(file)
    }

    override fun doesConfigFileExist(): Boolean = file.exists()

}