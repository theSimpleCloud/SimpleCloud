package eu.thesimplecloud.lib.directorypaths

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.config.LauncherConfig
import java.io.File

class DirectoryPathManager {

    init {
        val launcherConfigFile = File("launcher.json")
        if (!launcherConfigFile.exists()) {
            DirectoryPaths.paths = DirectoryPaths()
            JsonData().append("data", LauncherConfig(DirectoryPaths.paths)).saveAsFile(launcherConfigFile)
        } else {
            val launcherConfig = JsonData.fromJsonFile(launcherConfigFile).getObject("data", LauncherConfig::class.java)
            if (launcherConfig != null) {
                DirectoryPaths.paths = launcherConfig.directoryPaths
            }
        }
    }

}