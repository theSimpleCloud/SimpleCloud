package eu.thesimplecloud.launcher.updater

class UpdateExecutor {

    fun executeUpdateIfAvailable(updater: IUpdater) {
        if (updater.isUpdateAvailable()) {
            updater.downloadJarsForUpdate()
            updater.executeJar()
        }
    }

}