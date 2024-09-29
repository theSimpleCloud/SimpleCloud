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


import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.runner.RunnerFileProvider
import eu.thesimplecloud.runner.dependency.AdvancedCloudDependency
import eu.thesimplecloud.runner.utils.WebContentLoader
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class RunnerUpdater(
    private val updateChannel: UpdateChannel
) : AbstractUpdater(
    "eu.thesimplecloud.simplecloud",
    "simplecloud-runner",
    File("runner-update.jar")
) {

    private val dependencyLoaderFile = File("storage", "dependency-loader.jar")

    private var versionToInstall: String? = null

    override fun getCurrentVersion(): String {
        return getCurrentLauncherVersion()
    }

    override fun getVersionToInstall(): String? {
        if (versionToInstall != null) return versionToInstall
        //TODO update to new url
        val urlstring =
            "https://update.thesimplecloud.eu/latestVersion/${getCurrentVersion()}?channel=${updateChannel.toChannelString()}"
        val content =
            WebContentLoader().loadContent(urlstring)
                ?: return null
        this.versionToInstall = JsonLib.fromJsonString(content).getString("latestVersion")
        return this.versionToInstall
    }

    override fun executeJar() {
        val file = File("runner-update.jar")
        val currentRunnerFile = RunnerFileProvider.RUNNER_FILE
        val newClassLoader = URLClassLoader(arrayOf(file.toURI().toURL()))
        Thread.currentThread().contextClassLoader = newClassLoader
        Runtime.getRuntime().addShutdownHook(Thread {
            Thread.sleep(200)
            if (Launcher.instance.isWindows()) {
                performWindowsUpdate(currentRunnerFile, file)
            } else {
                performLinuxUpdate(currentRunnerFile, file)
            }
            println("Update installed. Stopping in 5 seconds...")
            Thread.sleep(5 * 1000)
        })
        Launcher.instance.shutdown()
    }

    private fun performWindowsUpdate(currentRunnerFile: File, updateRunnerFile: File) {
        val updaterFile = File("storage/updater.jar")
        val dependency =
            AdvancedCloudDependency("eu.thesimplecloud.simplecloud", "simplecloud-updater", getVersionToInstall()!!)
        dependency.download(getRepositoryURL(), updaterFile)
        val processBuilder = ProcessBuilder(
            "java",
            "-jar",
            "storage/updater.jar",
            "300",
            currentRunnerFile.absolutePath,
            updateRunnerFile.absolutePath,
            Launcher.instance.getLauncherFile().absolutePath,
            this.dependencyLoaderFile.absolutePath
        )
        processBuilder.directory(File("."))
        processBuilder.start()
    }

    private fun performLinuxUpdate(currentRunnerFile: File, file: File) {
        Files.move(file.toPath(), currentRunnerFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        deleteLauncherAndDependencyLoader()
    }

    private fun deleteLauncherAndDependencyLoader() {
        Launcher.instance.getLauncherFile().delete()
        this.dependencyLoaderFile.delete()
    }
}