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

import java.io.File
import java.net.URLClassLoader
import java.nio.file.Files

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 11.06.2021
 * Time: 20:17
 */

private val copiedLauncherFile = File("launcher.jar")

fun main(args: Array<String>) {
    copyLauncherOutOfJar()

    val loadedDependencyFiles = LauncherDependencyLoader().loadLauncherDependencies()
    val loadedDependencyURLs = loadedDependencyFiles.map { it.toURI().toURL() }.toTypedArray()

    val currentContextClassLoader = Thread.currentThread().contextClassLoader
    val classLoader =
        URLClassLoader(arrayOf(copiedLauncherFile.toURI().toURL(), *loadedDependencyURLs), currentContextClassLoader)

    Thread.currentThread().contextClassLoader = classLoader

    executeLauncherMain(classLoader, args)
}

fun copyLauncherOutOfJar() {
    val launcherResource = DependencyLoader::class.java.getResourceAsStream("/launcher.jar")
    if (!copiedLauncherFile.exists())
        Files.copy(launcherResource, copiedLauncherFile.toPath())
}

private fun executeLauncherMain(classLoader: URLClassLoader, args: Array<String>) {
    val loadedClass = classLoader.loadClass("eu.thesimplecloud.launcher.startup.LauncherMainKt")
    val method = loadedClass.getMethod("main", Array<String>::class.java)
    method.invoke(null, args)
}
