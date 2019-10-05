package eu.thesimplecloud.launcher.dependency

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
                        println("Downloaded ${dependency.getDownloadURL(repoUrl)}")
                    } catch (ex: FileNotFoundException) {
                        println("Can not download ${dependency.getDownloadURL(repoUrl)}")
                    }
                }
            }
        }
        val urlClassLoader = ClassLoader.getSystemClassLoader() as URLClassLoader
        val method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
        method.isAccessible = true
        dependencies.forEach { depedency ->
            depedency.getDownloadedFile()
        }
    }

}