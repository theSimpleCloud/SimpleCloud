package eu.thesimplecloud.base.manager.config

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import java.io.File
import java.lang.IllegalStateException

class ManagerConfigLoader {

    private val file = File(DirectoryPaths.paths.configurationsPath, "manager.json")

    fun loadConfig(): ManagerConfig {
        check(file.exists()) { "Error while loading manager config: File does not exist." }
        return JsonData.fromJsonFile(file).getObject(ManagerConfig::class.java) ?: throw IllegalStateException("Error while loading manager config: Couldn't parse file.")
    }

    fun saveConfig(managerConfig: ManagerConfig) {
        JsonData.fromObject(managerConfig).saveAsFile(file)
    }

    fun doesFileExist(): Boolean = file.exists()

}