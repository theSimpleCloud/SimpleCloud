package eu.thesimplecloud.launcher.config

import eu.thesimplecloud.lib.config.IConfig
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths

class LauncherConfig(var host: String, val port: Int, val directoryPaths: DirectoryPaths) : IConfig {
}