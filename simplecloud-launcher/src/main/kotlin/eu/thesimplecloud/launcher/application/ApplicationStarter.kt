package eu.thesimplecloud.launcher.application

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.launcher.exception.module.ModuleHandler
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.updater.BaseUpdater
import eu.thesimplecloud.launcher.updater.UpdateExecutor
import java.io.File

class ApplicationStarter {

    fun startApplication(applicationType: CloudApplicationType) {
        //set thread class loader to load the base with the same class loader
        Thread.currentThread().contextClassLoader = Launcher.instance.currentClassLoader
        val moduleLoader = ModuleHandler()
        moduleLoader.setCreateModuleClassLoader { urls, name -> ApplicationClassLoader(urls, Launcher.instance.currentClassLoader, name, moduleLoader) }
        val moduleFileName = applicationType.name.toLowerCase() + ".json"
        //Launcher.instance.consoleManager.stopThread()
        Launcher.instance.consoleManager.applicationName = applicationType.getApplicationName()
        //Launcher.instance.consoleManager.startThread()
        val file = File(DirectoryPaths.paths.storagePath + "base.jar")

        if (!Launcher.instance.launcherStartArguments.disableAutoUpdater || !file.exists()) {
            Launcher.instance.consoleSender.sendMessage("launcher.base.checking-for-updates", "Checking for base updates...")
            val appFileContent = moduleLoader.loadModuleFileContent(file, moduleFileName)

            val mainClass = if (file.exists()) moduleLoader.loadModuleClass(Launcher.instance.currentClassLoader, appFileContent.mainClass) else null
            val updater = BaseUpdater(mainClass)
            if (updater.isUpdateAvailable()) {
                Launcher.instance.consoleSender.sendMessage("launcher.base.update-found",
                        "Found base update %VERSION%", updater.getVersionToInstall()!!, " (current: %CURRENT_VERSION%", updater.getCurrentVersion(), ")")
                Launcher.instance.consoleSender.sendMessage("launcher.base.downloading-update", "Downloading base update...")
                UpdateExecutor().executeUpdate(updater)
            } else {
                Launcher.instance.consoleSender.sendMessage("launcher.base.newest-version", "You are running on the latest version.")
            }
        }

        val cloudModule = moduleLoader.loadModule(file, moduleFileName).cloudModule as ICloudApplication
        Launcher.instance.activeApplication = cloudModule
    }


}