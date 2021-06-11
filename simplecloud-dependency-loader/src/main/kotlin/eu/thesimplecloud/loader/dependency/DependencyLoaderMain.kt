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
    val classLoader = URLClassLoader(arrayOf(copiedLauncherFile.toURI().toURL(), *loadedDependencyURLs), currentContextClassLoader)

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
