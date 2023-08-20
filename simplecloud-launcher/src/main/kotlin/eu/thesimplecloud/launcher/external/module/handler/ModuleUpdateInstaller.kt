/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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

package eu.thesimplecloud.launcher.external.module.handler

import eu.thesimplecloud.launcher.external.module.LoadedModuleFileContent
import eu.thesimplecloud.launcher.external.module.updater.ModuleUpdater
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.updater.UpdateExecutor

/**
 * Created by IntelliJ IDEA.
 * Date: 29.11.2020
 * Time: 18:28
 * @author Frederick Baier
 */
class ModuleUpdateInstaller(
    private val loadedModuleFileContent: LoadedModuleFileContent
) {

    /**
     * Installs the update if there is one
     * @return whether a update was installed
     */
    fun updateIfAvailable(): Boolean {
        val (_, content, updaterFileContent) = loadedModuleFileContent
        if (updaterFileContent != null && !Launcher.instance.launcherStartArguments.disableAutoUpdater) {
            val updater = ModuleUpdater(updaterFileContent, loadedModuleFileContent.file)
            if (updater.isUpdateAvailable()) {
                Launcher.instance.consoleSender.sendProperty("manager.module.updating", content.name)
                UpdateExecutor().executeUpdate(updater)
                Launcher.instance.consoleSender.sendProperty("manager.module.updated", content.name)
                return true
            }
        }
        return false
    }

}