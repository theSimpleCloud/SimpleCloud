package eu.thesimplecloud.launcher.updater

interface IUpdater {

    /**
     * Returns the latest version
     */
    fun getVersionToInstall(): String?

    /**
     * Returns the current version.
     */
    fun getCurrentVersion(): String

    /**
     * Returns whether an update is available
     */
    fun isUpdateAvailable(): Boolean = getCurrentVersion().isBlank() || (getVersionToInstall() != null && getVersionToInstall() != getCurrentVersion())

    /**
     * Downloads the jars needed to update
     */
    fun downloadJarsForUpdate()

    /**
     * Executes the jar to complete the update.
     */
    fun executeJar()

    /**
     * Returns the current launcher version
     */
    fun getCurrentLauncherVersion(): String {
        return System.getProperty("simplecloud.version")
    }

    /**
     * Returns the repository url to use
     */
    fun getRepositoryURL(): String {
        return if (getCurrentLauncherVersion().contains("SNAPSHOT")) {
            "https://repo.thesimplecloud.eu/artifactory/list/gradle-dev-local/"
        } else {
            "https://repo.thesimplecloud.eu/artifactory/list/gradle-release-local/"
        }
    }

}