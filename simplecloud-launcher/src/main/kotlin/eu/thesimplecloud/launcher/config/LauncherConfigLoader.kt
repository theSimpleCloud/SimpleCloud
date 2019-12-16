package eu.thesimplecloud.launcher.config

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.config.AbstractJsonDataConfigLoader
import eu.thesimplecloud.lib.config.IConfigLoader
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import java.io.File
import java.lang.IllegalStateException

class LauncherConfigLoader : AbstractJsonDataConfigLoader<LauncherConfig>(LauncherConfig::class.java, File("launcher.json"), { LauncherConfig("127.0.0.1", 1900, DirectoryPaths()) })