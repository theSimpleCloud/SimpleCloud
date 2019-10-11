package eu.thesimplecloud.base.manager.startup

import eu.thesimplecloud.base.manager.setup.ProxyJarSetup
import eu.thesimplecloud.base.manager.setup.ServerJarSetup
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import java.io.File

class MinecraftJars {

    fun checkJars() {
        File(DirectoryPaths.paths.minecraftJarsPath).mkdirs()
        val serverFile = File(DirectoryPaths.paths.minecraftJarsPath, "server.jar")
        val proxyFile = File(DirectoryPaths.paths.minecraftJarsPath, "proxy.jar")
        if (!proxyFile.exists())
            Launcher.instance.setupManager.queueSetup(ProxyJarSetup(proxyFile))
        if (!serverFile.exists())
            Launcher.instance.setupManager.queueSetup(ServerJarSetup(serverFile))
    }

}