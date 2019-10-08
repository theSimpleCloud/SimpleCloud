package eu.thesimplecloud.launcher.application

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.exception.ApplicationLoadException
import eu.thesimplecloud.launcher.external.ExtensionLoader
import eu.thesimplecloud.launcher.external.ResourceFinder
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File
import java.lang.Exception

class CloudApplicationStarter {

    fun startCloudApplication(cloudApplicationType: CloudApplicationType) {
        val cloudApplication = this.loadApplicationClass(File("SimpleCloud.jar"), cloudApplicationType)
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

    private fun loadApplicationClass(file: File, cloudApplicationType: CloudApplicationType): ICloudApplication {
        require(file.exists()) { "Specified file to load application from does not exist: ${file.path}" }
        val fileStream = ResourceFinder().findResource(file, "${cloudApplicationType.name.toLowerCase()}.json")
                ?: throw ApplicationLoadException("Error while loading application ${file.path}: No '${cloudApplicationType.name.toLowerCase()}.json' found.")
        val jsonData = JsonData.fromInputStream(fileStream)
        try {
            val applicationFileContent = jsonData.getObject(ApplicationFileContent::class.java)
                    ?: throw ApplicationLoadException("Error while loading application ${file.path}: Empty '${cloudApplicationType.name.toLowerCase()}.json'.")
            return ExtensionLoader<ICloudApplication>().loadClass(file, applicationFileContent.mainClass, ICloudApplication::class.java)
        } catch (ex: Exception) {
            throw ApplicationLoadException("Error while loading application ${file.path}.", ex)
        }
    }

}