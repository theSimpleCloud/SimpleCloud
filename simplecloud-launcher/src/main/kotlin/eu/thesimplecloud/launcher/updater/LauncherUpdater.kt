package eu.thesimplecloud.launcher.updater

import eu.thesimplecloud.lib.utils.Downloader
import eu.thesimplecloud.lib.update.IUpdater

class LauncherUpdater : IUpdater {

    //TODO get latest version from web
    override fun getLatestVersion(): String = LauncherUpdater::class.java.getPackage().implementationVersion

    override fun getCurrentVersion(): String = LauncherUpdater::class.java.getPackage().implementationVersion

    override fun downloadJarsForUpdate() {
        //TODO edit download url
        val downloadUrl = "http://repo.thesimplecloud.eu/artifactory/gradle-dev-local/eu/thesimplecloud/clientserverapi/clientserverapi/${getLatestVersion()}/clientserverapi-${getLatestVersion()}.jar"
        Downloader().userAgentDownload(downloadUrl, "Launcher.jar")
        //TODO download updater
    }

    override fun executeJar() {
        //TODO execute updater
    }
}