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

package eu.thesimplecloud.launcher.dependency

import eu.thesimplecloud.api.external.ResourceFinder
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.launcher.startup.Launcher
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Created by IntelliJ IDEA.
 * Date: 04.09.2020
 * Time: 17:49
 * @author Frederick Baier
 */
class DependencyLoader {

    companion object {
        val INSTANCE = DependencyLoader()
    }

    private val installedDependencies = CopyOnWriteArraySet<LauncherCloudDependency>()

    private val resolvedDependencies = CopyOnWriteArraySet<LauncherCloudDependency>()

    fun loadDependencies(repositories: List<String>, dependencies: List<LauncherCloudDependency>) {
        val allDependencies = dependencies.map { collectSubDependencies(it, repositories) }.flatten()
        val dependenciesByArtifactId = allDependencies.groupBy { it.artifactId }.values
        val newestVersions = dependenciesByArtifactId.map {
            it.reduce { acc, dependency -> dependency.getDependencyWithNewerVersion(acc) }
        }
        newestVersions.forEach {
            installDependency(it)
        }
    }

    private fun collectSubDependencies(dependency: LauncherCloudDependency, repositories: List<String>, list: MutableList<LauncherCloudDependency> = ArrayList()): List<LauncherCloudDependency> {
        if (this.resolvedDependencies.contains(dependency)) return list
        this.resolvedDependencies.add(dependency)
        list.add(dependency)
        resolveDependencyFilesIfNotExist(dependency, repositories)
        loggerMessage("Loading dependency ${dependency.getName()}")
        val subDependencies = getSubDependenciesOfDependency(dependency)
        subDependencies.forEach { collectSubDependencies(it, repositories, list) }
        return list
    }

    private fun installDependency(dependency: LauncherCloudDependency) {
        if (this.installedDependencies.contains(dependency)) return
        this.installedDependencies.add(dependency)
        ResourceFinder.addToClassLoader(dependency.getDownloadedFile())
        loggerMessage("Installed dependency ${dependency.getName()}")
    }

    private fun resolveDependencyFilesIfNotExist(dependency: LauncherCloudDependency, repositories: List<String>) {
        if (!dependency.getDownloadedInfoFile().exists()) {
            SimpleDependencyDownloader(repositories).downloadFiles(dependency)
        }
    }

    private fun getSubDependenciesOfDependency(dependency: LauncherCloudDependency): List<LauncherCloudDependency> {
        val infoFile = dependency.getDownloadedInfoFile()
        val subDependencies = JsonLib.fromJsonFile(infoFile)!!
                .getObject(Array<LauncherCloudDependency>::class.java)
        return subDependencies.asList()
    }

    private fun loggerMessage(message: String) {
        if (isLoggerAvailable()) {
            Launcher.instance.logger.console(message)
        } else {
            println(message)
        }
    }

    private fun isLoggerAvailable() =
            try {
                Launcher.instance.logger
                true
            } catch (ex: Exception) {
                false
            }

    fun getInstalledDependencies(): Collection<LauncherCloudDependency> {
        return this.installedDependencies
    }
}