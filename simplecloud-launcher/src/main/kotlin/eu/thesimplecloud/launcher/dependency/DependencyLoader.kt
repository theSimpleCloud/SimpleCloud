package eu.thesimplecloud.launcher.dependency

import eu.thesimplecloud.launcher.exception.DependencyException
import eu.thesimplecloud.launcher.external.ResourceFinder
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.depedency.Dependency
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.net.URLClassLoader

class DependencyLoader(val repositories: List<String>) {

    fun installDependencies(dependencies: List<Dependency>) {
        val loggerAvailable = try {
            Launcher.instance.logger
            true
        } catch (ex: Exception) {
            false
        }
        val file = File("dependencies/")
        file.mkdirs()
        repositories.forEach { repoUrl ->
            dependencies.forEach { dependency ->
                if (!dependency.getDownloadedFile().exists()) {
                    try {
                        dependency.download(repoUrl)
                        if (loggerAvailable)
                            Launcher.instance.logger.console("Downloaded dependency ${dependency.getDownloadURL(repoUrl)}")
                        else
                            println("Downloaded dependency ${dependency.getDownloadURL(repoUrl)}")
                    } catch (ex: FileNotFoundException) {

                    }
                }
            }
        }
        dependencies.filter { !it.getDownloadedFile().exists() }
                .forEach {
                    if (loggerAvailable)
                        Launcher.instance.logger.warning("Failed to download dependency ${it.groupId}:${it.artifactId}:${it.version}")
                    else
                        println("Failed to download dependency ${it.groupId}:${it.artifactId}:${it.version}")
                }
        if (dependencies.any { !it.getDownloadedFile().exists() }) throw DependencyException("Failed to load all dependencies.")



        dependencies.forEach { ResourceFinder.addToClassPath(it.getDownloadedFile()) }

        if (loggerAvailable)
            Launcher.instance.logger.success("Installed dependencies successfully.")
        else
            println("Installed dependencies successfully.")
    }

}