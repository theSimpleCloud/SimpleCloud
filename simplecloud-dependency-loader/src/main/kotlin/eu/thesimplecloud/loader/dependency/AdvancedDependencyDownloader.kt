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

package eu.thesimplecloud.loader.dependency

import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.runner.dependency.AdvancedCloudDependency
import eu.thesimplecloud.runner.dependency.CloudDependency
import eu.thesimplecloud.runner.dependency.SimpleDependencyDownloader
import org.eclipse.aether.artifact.DefaultArtifact
import java.io.IOException

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 11.06.2021
 * Time: 21:18
 */
class AdvancedDependencyDownloader(repositories: List<String>) : SimpleDependencyDownloader(repositories) {

    fun downloadFiles(dependency: AdvancedCloudDependency) {
        if (dependency.getDownloadedFile().exists()) return
        this.repositories.forEach { repoUrl ->
            try {
                downloadAnyways(dependency, repoUrl)
                return
            } catch (e: Exception) {
                //ignore because the repository was wrong and another repository will be correct
            }
        }

        throw IllegalArgumentException("No valid repository was found for ${dependency.getName()} repos: $repositories")
    }

    @Throws(IOException::class)
    private fun downloadAnyways(dependency: AdvancedCloudDependency, repoUrl: String) {
        dependency.download(repoUrl)
        resolveDependenciesAndSaveToInfoFile(dependency, repoUrl)
    }

    private fun resolveDependenciesAndSaveToInfoFile(dependency: AdvancedCloudDependency, repoUrl: String) {
        val aetherArtifact = DefaultArtifact("${dependency.groupId}:${dependency.artifactId}:${dependency.version}")
        val dependencies = DependencyResolver(repoUrl, aetherArtifact).collectDependencies()
        val cloudDependencies = dependencies.map { it.artifact }
            .map { CloudDependency(it.groupId, it.artifactId, it.version) }
        JsonLib.fromObject(cloudDependencies).saveAsFile(dependency.getDownloadedInfoFile())
    }

}