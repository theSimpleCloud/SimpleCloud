package eu.thesimplecloud.launcher.updater

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import java.io.File
import java.util.jar.JarFile

class BaseUpdater : AbstractUpdater(
        "eu.thesimplecloud.simplecloud",
        "simplecloud-base",
        File(DirectoryPaths.paths.storagePath + "base.jar")
) {

    override fun getVersionToInstall(): String? {
        return getCurrentLauncherVersion()
    }

    override fun getCurrentVersion(): String {
        //return empty string because it will be unequal to the newest base version
        if (!updateFile.exists()) return "NOT_INSTALLED"
        val jarVersion = JarFile(updateFile)
        return jarVersion.manifest.mainAttributes.getValue("Implementation-Version")
    }

    override fun executeJar() {
        //do nothing
    }
}