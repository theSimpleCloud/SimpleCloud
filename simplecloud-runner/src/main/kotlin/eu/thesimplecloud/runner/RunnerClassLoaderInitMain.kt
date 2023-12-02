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

package eu.thesimplecloud.runner

import eu.thesimplecloud.runner.dependency.AdvancedCloudDependency
import eu.thesimplecloud.runner.dependency.DependencyLoaderStartup
import eu.thesimplecloud.runner.utils.Downloader
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.charset.StandardCharsets

/**
 * Created by IntelliJ IDEA.
 * Date: 29.08.2020
 * Time: 07:46
 * @author Frederick Baier
 */

private val copiedDependencyLoaderFile = File("storage", "dependency-loader.jar")
private val copiedLauncherFile = File("launcher.jar")
private val copiedSimpleCloudPluginFile = File("storage/pluginJars", "SimpleCloud-Plugin-${getCloudVersion()}.jar")
private val copiedSimpleCloudExtensionFile = File("storage/pluginJars", "SimpleCloud-Extension-${getCloudVersion()}.jar")

private val lastStartedVersionFile = File("storage/versions", "lastStartedVersion.json")
private val dependenciesDir = File("dependencies/")

fun main(args: Array<String>) {
    val version = getCloudVersion()
    val lastStartedVersion = getLastStartedVersion()

    if (version != lastStartedVersion && dependenciesDir.exists()) {
        println("Deleting dependencies directory...")
        dependenciesDir.deleteRecursively()
    }


    if (!version.contains("SNAPSHOT")) {
        if (!copiedDependencyLoaderFile.exists())
            downloadJarFromDependency("simplecloud-dependency-loader", copiedDependencyLoaderFile)

        if (!copiedSimpleCloudPluginFile.exists()) {
            Downloader().userAgentDownload(
                "https://repo.thesimplecloud.eu/artifactory/gradle-release-local/eu/thesimplecloud/simplecloud/simplecloud-plugin/$version/simplecloud-plugin-$version-all.jar",
                copiedSimpleCloudPluginFile
            )
        }

        if (!copiedSimpleCloudExtensionFile.exists()) {
            Downloader().userAgentDownload(
                "https://repo.thesimplecloud.eu/artifactory/gradle-release-local/eu/thesimplecloud/simplecloud/simplecloud-extension/$version/simplecloud-extension-$version-all.jar",
                copiedSimpleCloudExtensionFile
            )
        }

        if (!copiedLauncherFile.exists())
            Downloader().userAgentDownload(
                "https://repo.thesimplecloud.eu/artifactory/gradle-release-local/eu/thesimplecloud/simplecloud/simplecloud-launcher/$version/simplecloud-launcher-$version-all.jar",
                copiedLauncherFile
            )
    }

    val dependencyLoaderStartup = DependencyLoaderStartup()

    val loadedDependencyFiles = dependencyLoaderStartup.loadDependenciesToResolveDependencies()
    val loadedDependencyUrls = loadedDependencyFiles.map { it.toURI().toURL() }.toTypedArray()

    val classLoader = initClassLoader(loadedDependencyUrls)
    Thread.currentThread().contextClassLoader = classLoader

    executeDependencyLoaderMain(classLoader, args)
}

private fun getLastStartedVersion(): String? {
    if (!lastStartedVersionFile.exists()) {
        return null
    }
    val lastStartedVersion = lastStartedVersionFile.readLines(StandardCharsets.UTF_8).first()
    return lastStartedVersion.replace("\"", "")
}

private fun getCloudVersion(): String {
    return RunnerClassLoader::class.java.`package`.implementationVersion
}

private fun downloadJarFromDependency(artifactId: String, file: File) {
    val dependency = AdvancedCloudDependency("eu.thesimplecloud.simplecloud", artifactId, getCloudVersion())
    dependency.download("https://repo.thesimplecloud.eu/artifactory/gradle-release-local/", file)
}

private fun executeDependencyLoaderMain(classLoader: URLClassLoader, args: Array<String>) {
    val loadedClass = classLoader.loadClass("eu.thesimplecloud.loader.dependency.DependencyLoaderMainKt")
    val method = loadedClass.getMethod("main", Array<String>::class.java)
    method.invoke(null, args)
}

private fun initClassLoader(loadedDependencies: Array<URL>): URLClassLoader {
    return RunnerClassLoader(
        arrayOf(copiedDependencyLoaderFile.toURI().toURL(), *loadedDependencies)
    )
}