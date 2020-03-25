package eu.thesimplecloud.launcher.updater

class UpdateExecutor {

    fun executeUpdate(updater: IUpdater) {
        updater.downloadJarsForUpdate()
        updater.executeJar()
    }

}