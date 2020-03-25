package eu.thesimplecloud.launcher.application

import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.external.module.CloudModuleLoader
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.updater.BaseUpdater
import eu.thesimplecloud.launcher.updater.UpdateExecutor
import java.io.File

class ApplicationStarter {

    fun startApplication(applicationType: CloudApplicationType) {
        val cloudModuleLoader = CloudModuleLoader()
        val moduleFileName = applicationType.name.toLowerCase() + ".json"
        Launcher.instance.logger.applicationName = applicationType.getApplicationName()
        val file = File("SimpleCloud.jar")

        if (!Launcher.instance.launcherStartArguments.disableAutoUpdater || !file.exists()) {
            Launcher.instance.consoleSender.sendMessage("launcher.base.checking-for-updates", "Checking for base updates...")
            val mainClass = if (file.exists()) cloudModuleLoader.loadMainClass(file, moduleFileName) else null
            val updater = BaseUpdater(mainClass)
            Launcher.instance.consoleSender.sendMessage(updater.getLatestVersion().toString())
            Launcher.instance.consoleSender.sendMessage(updater.getCurrentVersion().toString())
            if (updater.isUpdateAvailable()) {
                Launcher.instance.consoleSender.sendMessage("launcher.base.update-found",
                        "Found base update %VERSION%", updater.getLatestVersion()!!, " (current: %CURRENT_VERSION%", updater.getCurrentVersion(), ")")
                Launcher.instance.consoleSender.sendMessage("launcher.base.downloading-update", "Downloading base update...")
                UpdateExecutor().executeUpdateIfAvailable(updater)
            } else {
                Launcher.instance.consoleSender.sendMessage("launcher.base.newest-version", "You are running the latest version.")
            }
        }

        val cloudModule = cloudModuleLoader.loadModule(file, moduleFileName).cloudModule
        cloudModule as ICloudApplication
        Launcher.instance.activeApplication = cloudModule
    }


}