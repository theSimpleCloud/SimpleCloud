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

package eu.thesimplecloud.launcher.updater

import eu.thesimplecloud.launcher.startup.Launcher

interface IUpdater {

    /**
     * Returns the latest version
     */
    fun getVersionToInstall(): String?

    /**
     * Returns the current version.
     */
    fun getCurrentVersion(): String

    /**
     * Returns whether an update is available
     */
    fun isUpdateAvailable(): Boolean =
        getCurrentVersion().isBlank() || (getVersionToInstall() != null && getVersionToInstall() != getCurrentVersion())

    /**
     * Downloads the jars needed to update
     */
    fun downloadJarsForUpdate()

    /**
     * Executes the jar to complete the update.
     */
    fun executeJar()

    /**
     * Returns the current launcher version
     */
    fun getCurrentLauncherVersion(): String {
        return System.getProperty("simplecloud.version")
    }

    /**
     * Returns the repository url to use
     */
    fun getRepositoryURL(): String {
        return if (Launcher.instance.isSnapshotBuild()) {
            "https://repo.thesimplecloud.eu/artifactory/list/gradle-dev-local/"
        } else {
            "https://repo.thesimplecloud.eu/artifactory/list/gradle-release-local/"
        }
    }

}