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

import eu.thesimplecloud.loader.dependency.DependencyResolver
import eu.thesimplecloud.runner.dependency.AdvancedCloudDependency
import org.eclipse.aether.artifact.DefaultArtifact
import java.io.File
import java.util.jar.JarFile

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
            val aetherArtifact = DefaultArtifact("${groupId}:${artifactId}:(0,]")
            this.versionToInstall = DependencyResolver(getRepositoryURL(), aetherArtifact)
                .determineLatestVersion()
        }
        return this.versionToInstall
    }

    override fun downloadJarsForUpdate() {
        val latestVersion = getVersionToInstall()
            ?: throw RuntimeException("Cannot perform update. Is the server down? (repo: ${getRepositoryURL()})")
        val dependency = AdvancedCloudDependency(groupId, artifactId, latestVersion)
        dependency.download(getRepositoryURL(), updateFile)
    }

    fun getVersionFromManifestFile(file: File): String {
        val jarFile = JarFile(file)
        val version = jarFile.manifest.mainAttributes.getValue("Implementation-Version")
        jarFile.close()
        return version
    }


}