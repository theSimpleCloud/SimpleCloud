package eu.thesimplecloud.launcher.config

import eu.thesimplecloud.api.config.AbstractJsonDataConfigLoader
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import java.io.File

class LauncherConfigLoader : AbstractJsonDataConfigLoader<LauncherConfig>(LauncherConfig::class.java, File("launcher.json"), { LauncherConfig("127.0.0.1", 1900, DirectoryPaths()) })