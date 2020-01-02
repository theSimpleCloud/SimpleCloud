package eu.thesimplecloud.launcher.updater

import eu.thesimplecloud.api.utils.Downloader
import eu.thesimplecloud.api.update.IUpdater

class CloudUpdater : IUpdater {

    //TODO get latest version from web
    override fun getLatestVersion(): String = CloudUpdater::class.java.getPackage().implementationVersion
    //TODO get current version from file
    override fun getCurrentVersion(): String = CloudUpdater::class.java.getPackage().implementationVersion

    override fun downloadJarsForUpdate() {
        //TODO edit download url
        val downloadUrl = "http://repo.thesimplecloud.eu/artifactory/gradle-dev-local/eu/thesimplecloud/clientserverapi/clientserverapi/${getLatestVersion()}/clientserverapi-${getLatestVersion()}.jar"
        Downloader().userAgentDownload(downloadUrl, "SimpleCloud.jar")
    }

    override fun executeJar() {
        //Its not necessary to execute the jar here.
    }
}