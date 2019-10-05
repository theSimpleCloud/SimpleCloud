package eu.thesimplecloud.launcher.dependency

import eu.thesimplecloud.launcher.Launcher
import eu.thesimplecloud.launcher.exception.DependencyException
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.net.URLClassLoader

class DependencyLoader(val repositories: List<String>) {

    fun installDependencies(dependencies: List<Dependency>) {
        val file = File("dependencies/")
        println(file.absolutePath)
        file.mkdirs()
        repositories.forEach { repoUrl ->
            dependencies.forEach { dependency ->
                if (!dependency.getDownloadedFile().exists()) {
                    try {
                        dependency.download(repoUrl)
                        Launcher.instance.consoleSender.sendMessage("launcher.dependency.downloaded", "Downloaded dependency %DEPENDENCY%", dependency.getDownloadURL(repoUrl))
                    } catch (ex: FileNotFoundException) {

                    }
                }
            }
        }
        dependencies.filter { !it.getDownloadedFile().exists() }
                .forEach {  Launcher.instance.consoleSender.sendMessage("launcher.dependency.download-failed", "Failed to download dependency %DEPENDENCY%", "${it.groupId}:${it.artifactId}:${it.version}") }
        if (dependencies.any { !it.getDownloadedFile().exists() }) throw DependencyException("Failed to load all dependencies.")

        val urlClassLoader = ClassLoader.getSystemClassLoader() as URLClassLoader
        val method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
        method.isAccessible = true

        dependencies.forEach { method.invoke(urlClassLoader, it.getDownloadedFile().toURI().toURL()) }
        Launcher.instance.consoleSender.sendMessage("launcher.dependency.all-installed", "Installed all dependencies successfully.")
    }

}