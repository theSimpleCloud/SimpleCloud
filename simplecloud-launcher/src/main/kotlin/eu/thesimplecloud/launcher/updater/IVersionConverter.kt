package eu.thesimplecloud.launcher.updater

interface IVersionConverter {

    /**
     * Returns the version this converter converts from
     */
    fun getFromVersion(): String

    /**
     * Returns the version this converter converts to
     */
    fun getToVersion(): String

    /**
     * Coverts from [getFromVersion] to [getToVersion]
     */
    fun convert()

}