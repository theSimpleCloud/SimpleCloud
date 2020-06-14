/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.launcher.application

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.external.module.handler.ModuleHandler
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.updater.BaseUpdater
import eu.thesimplecloud.launcher.updater.UpdateExecutor
import java.io.File

class ApplicationStarter {

    fun startApplication(applicationType: CloudApplicationType) {
        //set thread class loader to load the base with the same class loader
        Thread.currentThread().contextClassLoader = Launcher.instance.currentClassLoader
        val moduleHandler = ModuleHandler()
        moduleHandler.setCreateModuleClassLoader { urls, name -> ApplicationClassLoader(urls, Launcher.instance.currentClassLoader, name, moduleHandler) }
        val moduleFileName = applicationType.name.toLowerCase() + ".json"
        //Launcher.instance.consoleManager.stopThread()
        //Launcher.instance.consoleManager.applicationName = applicationType.getApplicationName()
        //Launcher.instance.consoleManager.startThread()
        val file = File(DirectoryPaths.paths.storagePath + "base.jar")
        if (!Launcher.instance.launcherStartArguments.disableAutoUpdater || !file.exists()) {
            Launcher.instance.consoleSender.sendMessage("launcher.base.checking-for-updates", "Checking for base updates...")

            val updater = BaseUpdater()
            if (updater.isUpdateAvailable()) {
                Launcher.instance.consoleSender.sendMessage("launcher.base.update-found",
                        "Found base update %VERSION%", updater.getVersionToInstall()!!, " (current: %CURRENT_VERSION%", updater.getCurrentVersion(), ")")
                Launcher.instance.consoleSender.sendMessage("launcher.base.downloading-update", "Downloading base update...")
                UpdateExecutor().executeUpdate(updater)
            } else {
                Launcher.instance.consoleSender.sendMessage("launcher.base.newest-version", "You are running the latest version.")
            }
        }
        val cloudModule = moduleHandler.loadModule(file, moduleFileName).cloudModule as ICloudApplication
        Launcher.instance.activeApplication = cloudModule
    }


}