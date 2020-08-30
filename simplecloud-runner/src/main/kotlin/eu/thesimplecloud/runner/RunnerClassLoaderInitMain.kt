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

package eu.thesimplecloud.runner

import java.io.File
import java.net.URLClassLoader
import java.nio.file.Files

/**
 * Created by IntelliJ IDEA.
 * Date: 29.08.2020
 * Time: 07:46
 * @author Frederick Baier
 */

private val copiedLauncherFile = File("launcher.jar")

fun main(args: Array<String>) {
    copyLauncherOutOfJar()
    val classLoader = initClassLoader()
    Thread.currentThread().contextClassLoader = classLoader

    executeLauncherMain(classLoader, args)
}

fun copyLauncherOutOfJar() {
    val launcherResource = RunnerClassLoader::class.java.getResourceAsStream("/launcher.jar")
    if (!copiedLauncherFile.exists())
        Files.copy(launcherResource, copiedLauncherFile.toPath())
}

private fun executeLauncherMain(classLoader: URLClassLoader, args: Array<String>) {
    val loadedClass = classLoader.loadClass("eu.thesimplecloud.launcher.startup.LauncherMainKt")
    val method = loadedClass.getMethod("main", Array<String>::class.java)
    method.invoke(null, args)
}


private fun initClassLoader(): URLClassLoader {
    return RunnerClassLoader(
            arrayOf(copiedLauncherFile.toURI().toURL())
    )
}

private fun getRunningJarFile(): File {
    return File(RunnerClassLoader::class.java.protectionDomain.codeSource.location.toURI())
}