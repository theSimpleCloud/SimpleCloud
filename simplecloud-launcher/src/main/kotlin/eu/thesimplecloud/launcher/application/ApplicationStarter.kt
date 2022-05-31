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
import eu.thesimplecloud.launcher.external.module.LoadedModuleFileContent
import eu.thesimplecloud.launcher.external.module.handler.ModuleHandler
import eu.thesimplecloud.launcher.external.module.handler.UnsafeModuleLoader
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.updater.BaseUpdater
import eu.thesimplecloud.launcher.updater.UpdateExecutor
import java.io.File
import java.net.URL

class ApplicationStarter {
    //TODO replace properties
    fun startApplication(applicationType: CloudApplicationType) {
        val launcherClassLoader = Launcher.instance.currentClassLoader
        Thread.currentThread().contextClassLoader = launcherClassLoader
        val moduleHandler = ModuleHandler()
        val createClassLoaderFunction: (Array<URL>, String) -> ClassLoader =
            { urls, name -> ApplicationClassLoader(urls, launcherClassLoader, name, moduleHandler) }
        val moduleFileName = applicationType.name.lowercase() + ".json"
        val file = File(DirectoryPaths.paths.storagePath + "base.jar")
        if (!Launcher.instance.launcherStartArguments.disableAutoUpdater || !file.exists()) {
            Launcher.instance.consoleSender.sendMessage("Checking for base updates...")
            val updater = BaseUpdater()
            if (updater.isUpdateAvailable()) {
                Launcher.instance.consoleSender.sendMessage("Base update found: ${updater.getVersionToInstall()!!} (current: ${updater.getCurrentVersion()})")
                Launcher.instance.consoleSender.sendMessage("Downloading base update...")
                UpdateExecutor().executeUpdate(updater)
            } else {
                Launcher.instance.consoleSender.sendMessage("You are running the latest version of SimpleCloud.")
            }
            Launcher.instance.consoleSender.sendMessage("Thank you for using Mischmaschine's SimpleCloud fork!")
        }
        val moduleFileContent = moduleHandler.loadModuleFileContent(file, moduleFileName)
        val loadedModuleFileContent = LoadedModuleFileContent(file, moduleFileContent, null)
        val loadedModule = UnsafeModuleLoader(createClassLoaderFunction).loadModule(loadedModuleFileContent)


        val cloudModule = loadedModule.cloudModule as ICloudApplication
        cloudModule.onEnable()
        Launcher.instance.activeApplication = cloudModule
    }


}