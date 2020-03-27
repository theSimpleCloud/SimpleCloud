package eu.thesimplecloud.launcher.external.module

import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.external.ResourceFinder
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.dependency.DependencyLoader
import eu.thesimplecloud.launcher.exception.CloudModuleException
import eu.thesimplecloud.launcher.external.ExtensionLoader
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile

class CloudModuleLoader {

    @Throws(CloudModuleException::class)
    fun loadMainClass(file: File, moduleFileName: String): Class<out ICloudModule> {
        try {
            val cloudModuleFileContent = loadModuleFileContent(file, moduleFileName)
            return ExtensionLoader<ICloudModule>().loadClass(file, cloudModuleFileContent.mainClass, ICloudModule::class.java)
        } catch (ex: Exception) {
            throw CloudModuleException("An error occurred while loading module: ${file.path}", ex)
        }
    }

    @Throws(CloudModuleException::class)
    fun loadModule(file: File, moduleFileName: String): CloudModuleData {
        try {
            val cloudModuleFileContent = loadModuleFileContent(file, moduleFileName)
            ResourceFinder.addToClassLoader(file)
            val dependencyLoader = DependencyLoader.INSTANCE
            dependencyLoader.addRepositories(cloudModuleFileContent.repositories)
            dependencyLoader.addDependencies(cloudModuleFileContent.dependencies)
            dependencyLoader.installDependencies()
            val cloudModule = this.loadModuleClass(file, cloudModuleFileContent.mainClass)
            cloudModule.onEnable()
            return CloudModuleData(cloudModule, file, cloudModuleFileContent)
        } catch (ex: Exception) {
            throw CloudModuleException("An error occurred while loading module: ${file.path}", ex)
        }
    }


    private fun loadModuleClass(file: File, mainClass: String): ICloudModule {
        //exception will be caught in function above
        return ExtensionLoader<ICloudModule>().loadClassInstance(file, mainClass, ICloudModule::class.java)
    }

    fun loadModuleFileContent(file: File, moduleFileName: String): CloudModuleFileContent {
        require(file.exists()) { "Specified file to load module from does not exist: ${file.path}" }
        try {
            val jar = JarFile(file)
            val entry: JarEntry = jar.getJarEntry(moduleFileName)
                    ?: throw CloudModuleException("An error occurred while loading module ${file.path}: No '$moduleFileName.json' found.")
            val fileStream = jar.getInputStream(entry)
            val jsonData = JsonData.fromInputStream(fileStream)
            return jsonData.getObjectOrNull(CloudModuleFileContent::class.java)
                    ?: throw CloudModuleException("An error occurred while loading module ${file.path}: Invalid '$moduleFileName.json'.")
        } catch (ex: Exception) {
            throw CloudModuleException("An error occurred while loading module ${file.path}:", ex)
        }
    }

}