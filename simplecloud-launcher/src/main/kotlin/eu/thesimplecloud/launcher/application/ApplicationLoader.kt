package eu.thesimplecloud.launcher.application

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.exception.ApplicationLoadException
import eu.thesimplecloud.launcher.external.ExtensionLoader
import eu.thesimplecloud.launcher.external.ResourceFinder
import java.io.File
import java.lang.Exception

class ApplicationLoader {

    fun loadApplicationClass(file: File, cloudApplicationType: CloudApplicationType): ICloudApplication {
        val fileStream = ResourceFinder().findResource(file, "${cloudApplicationType.name.toLowerCase()}.json")
                ?: throw ApplicationLoadException("Error while loading application ${file.path}: No 'application.json' found.")
        val jsonData = JsonData.fromInputStream(fileStream)
        try {
            val applicationFileContent = jsonData.getObject(ApplicationFileContent::class.java)
                    ?: throw ApplicationLoadException("Error while loading application ${file.path}: Empty 'application.json'.")
            return ExtensionLoader<ICloudApplication>().loadClass(file, applicationFileContent.mainClass, ICloudApplication::class.java)
        } catch (ex: Exception) {
            throw ApplicationLoadException("Error while loading application ${file.path}.", ex)
        }
    }

}