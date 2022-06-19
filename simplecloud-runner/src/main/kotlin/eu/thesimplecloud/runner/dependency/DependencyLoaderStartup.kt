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

package eu.thesimplecloud.runner.dependency

import eu.thesimplecloud.jsonlib.JsonLib
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 11.06.2021
 * Time: 21:02
 */
class DependencyLoaderStartup {

    fun loadDependenciesToResolveDependencies(): Set<File> {
        println("Loading dependencies...")
        val downloader = SimpleDependencyDownloader(listOf("https://repo.maven.apache.org/maven2/"))

        val dependencies = listOf(
            AdvancedCloudDependency.fromCoords("org.eclipse.aether:aether-impl:1.1.0"),
            AdvancedCloudDependency.fromCoords("org.eclipse.aether:aether-api:1.1.0"),
            AdvancedCloudDependency.fromCoords("org.eclipse.aether:aether-spi:1.1.0"),
            AdvancedCloudDependency.fromCoords("org.eclipse.aether:aether-util:1.1.0"),
            AdvancedCloudDependency.fromCoords("org.eclipse.aether:aether-connector-basic:1.1.0"),
            AdvancedCloudDependency.fromCoords("org.eclipse.aether:aether-transport-file:1.1.0"),
            AdvancedCloudDependency.fromCoords("org.eclipse.aether:aether-transport-http:1.1.0"),
            AdvancedCloudDependency.fromCoords("org.apache.httpcomponents:httpclient:4.3.5"),
            AdvancedCloudDependency.fromCoords("org.apache.httpcomponents:httpcore:4.3.2"),
            AdvancedCloudDependency.fromCoords("commons-logging:commons-logging:1.1.3"),
            AdvancedCloudDependency.fromCoords("commons-codec:commons-codec:1.6"),
            AdvancedCloudDependency.fromCoords("org.apache.maven:maven-aether-provider:3.3.9"),
            AdvancedCloudDependency.fromCoords("org.apache.maven:maven-model:3.3.9"),
            AdvancedCloudDependency.fromCoords("org.codehaus.plexus:plexus-utils:3.0.22"),
            AdvancedCloudDependency.fromCoords("org.apache.commons:commons-lang3:3.4"),
            AdvancedCloudDependency.fromCoords("org.apache.maven:maven-model-builder:3.3.9"),
            AdvancedCloudDependency.fromCoords("org.codehaus.plexus:plexus-interpolation:1.21"),
            AdvancedCloudDependency.fromCoords("org.codehaus.plexus:plexus-component-annotations:1.6"),
            AdvancedCloudDependency.fromCoords("org.apache.maven:maven-artifact:3.3.9"),
            AdvancedCloudDependency.fromCoords("org.apache.maven:maven-builder-support:3.3.9"),
            AdvancedCloudDependency.fromCoords("com.google.guava:guava:21.0"),
            AdvancedCloudDependency.fromCoords("org.apache.maven:maven-repository-metadata:3.3.9"),
        )

        dependencies.forEach { downloader.downloadOnlyJar(it) }
        dependencies.forEach { createEmptyInfoFile(it) }

        return dependencies.map { it.getDownloadedFile() }.toSet()
    }

    private fun createEmptyInfoFile(dependency: AdvancedCloudDependency) {
        //creates an empty info file for the specified dependency to avoid bugs when the dependency is loaded again by another dependency
        JsonLib.fromObject(emptyArray<String>()).saveAsFile(dependency.getDownloadedInfoFile())
    }

}