package eu.thesimplecloud.launcher.application

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.dependency.DependencyLoader
import eu.thesimplecloud.launcher.exception.ApplicationLoadException
import eu.thesimplecloud.launcher.external.ExtensionLoader
import eu.thesimplecloud.launcher.external.ResourceFinder
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File
import kotlin.Exception

class CloudApplicationStarter {

    fun startCloudApplication(cloudApplicationType: CloudApplicationType) {
        val file = File("SimpleCloud.jar")
        val applicationFileContent = loadApplicationFileContent(file, cloudApplicationType)
        val cloudApplication = this.loadApplicationClass(file, applicationFileContent.mainClass)
        val dependencyLoader = DependencyLoader(applicationFileContent.repositories)
        try {
            dependencyLoader.installDependencies(applicationFileContent.dependencies)
        } catch (ex: Exception){
            Launcher.instance.shutdown()
            return
        }
        Launcher.instance.logger.applicationName = cloudApplicationType.getApplicationName()
        Launcher.instance.activeApplication = cloudApplication
        cloudApplication.start()
        when (cloudApplicationType) {
            CloudApplicationType.WRAPPER -> {
            }
            CloudApplicationType.MANAGER -> {
            }
        }

    }

    private fun loadApplicationClass(file: File, mainClass: String): ICloudApplication {
        try {
            return ExtensionLoader<ICloudApplication>().loadClass(file, mainClass, ICloudApplication::class.java)
        } catch (ex: Exception) {
            throw ApplicationLoadException("Error while loading application ${file.path}.", ex)
        }
    }

    fun loadApplicationFileContent(file: File, cloudApplicationType: CloudApplicationType): ApplicationFileContent {
        require(file.exists()) { "Specified file to load application from does not exist: ${file.path}" }
        val fileStream = ResourceFinder().findResource(file, "${cloudApplicationType.name.toLowerCase()}.json")
                ?: throw ApplicationLoadException("Error while loading application ${file.path}: No '${cloudApplicationType.name.toLowerCase()}.json' found.")
        val jsonData = JsonData.fromInputStream(fileStream)
        val applicationFileContent = jsonData.getObject(ApplicationFileContent::class.java)
                ?: throw ApplicationLoadException("Error while loading application ${file.path}: Empty '${cloudApplicationType.name.toLowerCase()}.json'.")
        return applicationFileContent

    }

}