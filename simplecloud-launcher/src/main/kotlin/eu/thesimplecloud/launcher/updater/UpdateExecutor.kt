package eu.thesimplecloud.launcher.updater

class UpdateExecutor {

    fun executeUpdateIfAvailable(updater: IUpdater) {
        if (updater.updateAvailable()) {
            updater.downloadJarsForUpdate()
            updater.executeJar()
        }
    }

}