package eu.thesimplecloud.launcher.updater

import java.io.File

class BaseUpdater(private val baseClass: Class<*>?) : AbstractUpdater(
        "eu.thesimplecloud.simplecloud",
        "simplecloud-base",
        File("SimpleCloud.jar")
) {

    override fun getVersionToInstall(): String? {
        return getCurrentLauncherVersion()
    }

    override fun getCurrentVersion(): String {
        //return empty string because it will be unequal to the newest base version
        return baseClass?.getPackage()?.implementationVersion ?: "NOT_INSTALLED"
    }

    override fun executeJar() {
        //do nothing
    }
}