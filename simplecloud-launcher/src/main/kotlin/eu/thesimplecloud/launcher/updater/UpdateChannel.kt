package eu.thesimplecloud.launcher.updater

enum class UpdateChannel {

    RELEASE, PRERELEASE;

    fun toChannelString(): String {
        return this.name.lowercase()
    }

}