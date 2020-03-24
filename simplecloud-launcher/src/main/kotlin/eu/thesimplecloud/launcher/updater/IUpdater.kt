package eu.thesimplecloud.launcher.updater

interface IUpdater {

    /**
     * Returns the latest version
     */
    fun getLatestVersion(): String?

    /**
     * Returns the current version.
     */
    fun getCurrentVersion(): String

    /**
     * Returns whether an update is available
     */
    fun updateAvailable(): Boolean = getLatestVersion() != getCurrentVersion()

    /**
     * Downloads the jars needed to update
     */
    fun downloadJarsForUpdate()

    /**
     * Executes the jar to complete the update.
     */
    fun executeJar()

}