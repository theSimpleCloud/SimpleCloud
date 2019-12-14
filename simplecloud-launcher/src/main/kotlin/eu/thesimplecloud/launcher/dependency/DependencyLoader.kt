package eu.thesimplecloud.launcher.dependency

import eu.thesimplecloud.launcher.exception.DependencyException
import eu.thesimplecloud.launcher.external.ResourceFinder
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.depedency.Dependency
import org.apache.maven.model.Model
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException


class DependencyLoader(val repositories: List<String>) {

    var loggerAvailable = false

    fun installDependencies(dependencies: List<Dependency>) {
        val file = File("dependencies/")
        file.mkdirs()
        checkDependenciesToDownloadDependencies()
        this.loggerAvailable = try {
            Launcher.instance.logger
            true
        } catch (ex: Exception) {
            false
        }
        if (this.loggerAvailable) Launcher.instance.logger.console("Starting loading pom information of dependencies...") else println("Starting loading pom information of dependencies...")
        val allDependencies = ArrayList<Dependency>()
        allDependencies.addAll(dependencies)
        dependencies.forEach { appendSubDependenciesOfDependency(it, allDependencies) }
        allDependencies.forEach { installDependency(it) }
        if (this.loggerAvailable)
            Launcher.instance.logger.success("Installed dependencies successfully.")
        else
            println("Installed dependencies successfully.")
    }

    private fun checkDependenciesToDownloadDependencies() {
        val modelDependency = Dependency("org.apache.maven", "maven-model", "3.3.9")
        val plexusDependency = Dependency("org.codehaus.plexus", "plexus-utils", "3.0.22")
        try {
            Class.forName("org.apache.maven.model.io.xpp3.MavenXpp3Reader")
        } catch (e: Exception) {
            installDependency(plexusDependency)
            installDependency(modelDependency)
        }

    }

    private fun appendSubDependenciesOfDependency(dependency: Dependency, dependencyList: MutableList<Dependency>) {
        for (repoURL in repositories) {
            val pomContent = dependency.getPomContent(repoURL) ?: continue
            val reader = MavenXpp3Reader()
            val model = reader.read(ByteArrayInputStream(pomContent.toByteArray()))
            for (mavenSubDependency in model.dependencies) {
                if (mavenSubDependency.version == null) {
                    continue
                }
                if (mavenSubDependency.groupId.contains("$") || mavenSubDependency.artifactId.contains("$")) {
                    continue
                }
                val newVersion = if (mavenSubDependency.version.contains("$")) {
                    getVersionOfPlaceHolder(model, mavenSubDependency)
                } else {
                    mavenSubDependency.version
                }
                if (!mavenSubDependency.isOptional && mavenSubDependency.scope != "test") {
                    val subDependency = Dependency(mavenSubDependency.groupId, mavenSubDependency.artifactId, newVersion)
                    dependencyList.add(subDependency)
                    appendSubDependenciesOfDependency(subDependency, dependencyList)
                }
            }
        }
    }

    private fun getVersionOfPlaceHolder(model: Model, mavenSubDependency: org.apache.maven.model.Dependency): String {
        val version = mavenSubDependency.version
        val editedVersion = version.dropLast(1).drop(2)
        return model.properties.getProperty(editedVersion)
    }

    private fun installDependency(dependency: Dependency) {
        if (!dependency.getDownloadedFile().exists()) {
            downloadDependency(dependency)
        }
        if (!dependency.getDownloadedFile().exists()) throw FileNotFoundException("Failed to download dependency ${dependency.artifactId}-${dependency.version}")
        ResourceFinder.addToClassPath(dependency.getDownloadedFile())
    }

    private fun downloadDependency(dependency: Dependency) {
        if (dependency.getDownloadedFile().exists()) return
        repositories.forEach { repoUrl ->
            try {
                dependency.download(repoUrl)
            } catch (ex: FileNotFoundException) {
                //ignore exception
            }
        }
        if (dependency.getDownloadedFile().exists()) {
            if (this.loggerAvailable)
                Launcher.instance.logger.console("Downloaded dependency ${dependency.artifactId}-${dependency.version}.jar")
            else
                println("Downloaded dependency ${dependency.artifactId}-${dependency.version}.jar")
        }
    }

}