package eu.thesimplecloud.launcher.updater

import java.io.File

class BaseUpdater(private val baseClass: Class<*>?) : AbstractUpdater(
        "eu.thesimplecloud.simplecloud",
        "simplecloud-base",
        "https://repo.thesimplecloud.eu/artifactory/list/gradle-release-local/",
        File("SimpleCloud.jar")
) {
    override fun getCurrentVersion(): String {
        //return empty string because it will be unequal to the newest base version
        return baseClass?.getPackage()?.implementationVersion ?: ""
    }

    override fun executeJar() {
        //do nothing
    }
}