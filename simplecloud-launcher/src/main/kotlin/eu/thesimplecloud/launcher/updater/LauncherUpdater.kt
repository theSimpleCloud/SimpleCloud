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

package eu.thesimplecloud.launcher.updater


import eu.thesimplecloud.api.depedency.Dependency
import eu.thesimplecloud.api.utils.ManifestLoader
import eu.thesimplecloud.launcher.LauncherMain
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class LauncherUpdater : AbstractUpdater(
        "eu.thesimplecloud.simplecloud",
        "simplecloud-launcher",
        File("launcher-update.jar")
) {

    override fun getCurrentVersion(): String {
        return getCurrentLauncherVersion()
    }

    override fun executeJar() {
        val file = File("launcher-update.jar")
        val runningJar = File(Launcher::class.java.protectionDomain.codeSource.location.toURI())
        val mainClass = ManifestLoader.getMainClass(file.absolutePath)
        val newClassLoader = URLClassLoader(arrayOf(file.toURI().toURL()))
        val mainMethod = newClassLoader.loadClass(mainClass).getMethod("main", Array<String>::class.java)
        Thread.currentThread().contextClassLoader = newClassLoader
        Runtime.getRuntime().addShutdownHook(Thread {
            Thread.sleep(200)
            if (!Launcher.instance.isWindows()) {
                performLinuxUpdate(file, runningJar)
            } else {
                performWindowsUpdate(runningJar, file)
            }
        })
        System.setProperty("simplecloud.launcher.update-mode", "true")
        System.setProperty("simplecloud.version", getVersionToInstall()!!)
        mainMethod.invoke(null, LauncherMain.specifiedArguments)
    }

    private fun performWindowsUpdate(runningJar: File, file: File) {
        val updaterFile = File("storage/updater.jar")
        val dependency = Dependency("eu.thesimplecloud.simplecloud", "simplecloud-updater", getVersionToInstall()!!)
        dependency.download(getRepositoryURL(), updaterFile)
        val processBuilder = ProcessBuilder("java", "-jar", "storage/updater.jar", "300", runningJar.absolutePath, file.absolutePath)
        processBuilder.directory(File("."))
        processBuilder.start()
    }

    private fun performLinuxUpdate(file: File, runningJar: File) {
        Files.move(file.toPath(), runningJar.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }
}