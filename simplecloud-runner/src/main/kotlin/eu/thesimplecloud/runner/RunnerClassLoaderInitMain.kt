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

import eu.thesimplecloud.runner.dependency.DependencyLoaderStartup
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files

/**
 * Created by IntelliJ IDEA.
 * Date: 29.08.2020
 * Time: 07:46
 * @author Frederick Baier
 */

private val copiedDependencyLoaderFile = File("storage", "dependency-loader.jar")

fun main(args: Array<String>) {
    copyDependencyLoaderOutOfJar()

    val dependencyLoaderStartup = DependencyLoaderStartup()

    val loadedDependencyFiles = dependencyLoaderStartup.loadDependenciesToResolveDependencies()
    val loadedDependencyUrls = loadedDependencyFiles.map { it.toURI().toURL() }.toTypedArray()

    val classLoader = initClassLoader(loadedDependencyUrls)
    Thread.currentThread().contextClassLoader = classLoader

    executeDependencyLoaderMain(classLoader, args)
}

fun copyDependencyLoaderOutOfJar() {
    val launcherResource = RunnerClassLoader::class.java.getResourceAsStream("/dependency-loader.jar")
    if (!copiedDependencyLoaderFile.exists()) {
        copiedDependencyLoaderFile.parentFile.mkdirs()
        Files.copy(launcherResource, copiedDependencyLoaderFile.toPath())
    }
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