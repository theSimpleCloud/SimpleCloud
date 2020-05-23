package eu.thesimplecloud.launcher.config

import eu.thesimplecloud.api.directorypaths.DirectoryPaths

data class LauncherConfig(
        var host: String,
        val port: Int,
        val directoryPaths: DirectoryPaths
)