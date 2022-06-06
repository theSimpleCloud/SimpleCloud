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

package eu.thesimplecloud.launcher.external.module.updater

import eu.thesimplecloud.launcher.external.module.update.UpdateMethod
import eu.thesimplecloud.launcher.external.module.update.UpdaterFileContent
import eu.thesimplecloud.launcher.updater.AbstractUpdater
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 12.06.2020
 * Time: 21:03
 * @author Frederick Baier
 */
class ModuleUpdater(
    private val updaterFileContent: UpdaterFileContent,
    moduleFile: File
) : AbstractUpdater(updaterFileContent.groupId, updaterFileContent.artifactId, moduleFile) {

    override fun getCurrentVersion(): String {
        return getVersionFromManifestFile(this.updateFile)
    }

    override fun executeJar() {
    }

    override fun getVersionToInstall(): String? {
        return when (updaterFileContent.updateMethod) {
            UpdateMethod.CLOUD -> getCurrentLauncherVersion()
            UpdateMethod.WEB -> super.getVersionToInstall()
        }
    }

    override fun getRepositoryURL(): String {
        val repository = updaterFileContent.repository
        if (repository.equals("CLOUD", true))
            return super.getRepositoryURL()
        return repository
    }
}