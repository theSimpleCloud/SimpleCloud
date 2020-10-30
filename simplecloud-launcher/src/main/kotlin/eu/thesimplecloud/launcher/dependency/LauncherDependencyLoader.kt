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


class LauncherDependencyLoader {

    fun loadLauncherDependencies() {
        println("Loading dependencies...")
        loadDependenciesToResolveDependencies()

        val dependencyLoader = DependencyLoader.INSTANCE
        dependencyLoader.loadDependencies(
                listOf("https://repo.maven.apache.org/maven2/", "https://repo.thesimplecloud.eu/artifactory/gradle-release-local/"),
                listOf(
                        LauncherCloudDependency("eu.thesimplecloud.clientserverapi", "clientserverapi", "4.0.2-SNAPSHOT"),
                        LauncherCloudDependency("org.slf4j", "slf4j-nop", "1.7.10"),
                        LauncherCloudDependency("org.fusesource.jansi", "jansi", "1.18"),
                        LauncherCloudDependency("org.jline", "jline", "3.14.0"),
                        LauncherCloudDependency("org.litote.kmongo", "kmongo", "3.11.2"),
                        LauncherCloudDependency("commons-io", "commons-io", "2.6"),
                        LauncherCloudDependency("org.slf4j", "slf4j-simple", "1.7.10"),
                        LauncherCloudDependency("com.google.guava", "guava", "21.0"),
                        LauncherCloudDependency("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.5"),
                        LauncherCloudDependency("com.google.code.gson", "gson", "2.8.6"),
                        LauncherCloudDependency("io.netty", "netty-all", "4.1.49.Final"),
                        LauncherCloudDependency("org.reflections", "reflections", "0.9.12"),
                        LauncherCloudDependency("org.mariadb.jdbc", "mariadb-java-client", "2.6.0"),
                        LauncherCloudDependency("com.github.ajalt", "clikt", "2.2.0")
                )
        )
    }

    private fun loadDependenciesToResolveDependencies() {
        val downloader = SimpleDependencyDownloader(listOf("https://repo.maven.apache.org/maven2/"))

        val dependencies = listOf(
                LauncherCloudDependency.fromCoords("org.eclipse.aether:aether-impl:1.1.0"),
                LauncherCloudDependency.fromCoords("org.eclipse.aether:aether-api:1.1.0"),
                LauncherCloudDependency.fromCoords("org.eclipse.aether:aether-spi:1.1.0"),
                LauncherCloudDependency.fromCoords("org.eclipse.aether:aether-util:1.1.0"),
                LauncherCloudDependency.fromCoords("org.eclipse.aether:aether-connector-basic:1.1.0"),
                LauncherCloudDependency.fromCoords("org.eclipse.aether:aether-transport-file:1.1.0"),
                LauncherCloudDependency.fromCoords("org.eclipse.aether:aether-transport-http:1.1.0"),
                LauncherCloudDependency.fromCoords("org.apache.httpcomponents:httpclient:4.3.5"),
                LauncherCloudDependency.fromCoords("org.apache.httpcomponents:httpcore:4.3.2"),
                LauncherCloudDependency.fromCoords("commons-logging:commons-logging:1.1.3"),
                LauncherCloudDependency.fromCoords("commons-codec:commons-codec:1.6"),
                LauncherCloudDependency.fromCoords("org.apache.maven:maven-aether-provider:3.3.9"),
                LauncherCloudDependency.fromCoords("org.apache.maven:maven-model:3.3.9"),
                LauncherCloudDependency.fromCoords("org.codehaus.plexus:plexus-utils:3.0.22"),
                LauncherCloudDependency.fromCoords("org.apache.commons:commons-lang3:3.4"),
                LauncherCloudDependency.fromCoords("org.apache.maven:maven-model-builder:3.3.9"),
                LauncherCloudDependency.fromCoords("org.codehaus.plexus:plexus-interpolation:1.21"),
                LauncherCloudDependency.fromCoords("org.codehaus.plexus:plexus-component-annotations:1.6"),
                LauncherCloudDependency.fromCoords("org.apache.maven:maven-artifact:3.3.9"),
                LauncherCloudDependency.fromCoords("org.apache.maven:maven-builder-support:3.3.9"),
                LauncherCloudDependency.fromCoords("com.google.guava:guava:21.0"),
                LauncherCloudDependency.fromCoords("org.apache.maven:maven-repository-metadata:3.3.9"),

                LauncherCloudDependency.fromCoords("com.google.code.gson:gson:2.8.6")
        )

        dependencies.forEach { downloader.downloadOnlyJar(it) }
        dependencies.forEach { ResourceFinder.addToClassLoader(it.getDownloadedFile()) }
        dependencies.forEach { createEmptyInfoFile(it) }

    }

    private fun createEmptyInfoFile(dependency: LauncherCloudDependency) {
        //creates an empty info file for the specified dependency to avoid bugs when the dependency is loaded again by another dependency
        JsonLib.fromObject(emptyArray<String>()).saveAsFile(dependency.getDownloadedInfoFile())
    }

}