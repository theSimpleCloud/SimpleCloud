/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
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

package eu.thesimplecloud.launcher.updater

import eu.thesimplecloud.api.depedency.Dependency
import eu.thesimplecloud.launcher.dependency.DependencyLoader
import java.io.File

abstract class AbstractUpdater(
        private val groupId: String,
        private val artifactId: String,
        protected val updateFile: File
) : IUpdater {

    private var versionToInstall: String? = null

    private var wasVersionToInstallCalled: Boolean = false

    override fun getVersionToInstall(): String? {
        if (!wasVersionToInstallCalled) {
            this.wasVersionToInstallCalled = true
            this.versionToInstall = DependencyLoader().getLatestVersionOfDependencyFromWeb(groupId, artifactId, getRepositoryURL())
        }
        return this.versionToInstall
    }

    override fun downloadJarsForUpdate() {
        val latestVersion = getVersionToInstall()
                ?: throw RuntimeException("Cannot perform update. Is the server down? (repo: ${getRepositoryURL()})")
        val dependency = Dependency(groupId, artifactId, latestVersion)
        dependency.download(getRepositoryURL(), updateFile)
    }


}