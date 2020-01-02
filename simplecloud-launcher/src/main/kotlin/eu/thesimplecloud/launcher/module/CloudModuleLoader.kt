package eu.thesimplecloud.launcher.module

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.resource.ResourceFinder
import eu.thesimplecloud.launcher.dependency.DependencyLoader
import eu.thesimplecloud.launcher.exception.CloudModuleLoadException
import eu.thesimplecloud.launcher.external.ExtensionLoader
import eu.thesimplecloud.api.external.ICloudModule
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import kotlin.Exception

class CloudModuleLoader {

    fun loadModule(file: File, moduleFileName: String): ICloudModule {
        try {
            val cloudModuleFileContent = loadModuleFileContent(file, moduleFileName)
            addModuleAsDependency(file)
            val dependencyLoader = DependencyLoader.INSTANCE
            dependencyLoader.addRepositories(cloudModuleFileContent.repositories)
            dependencyLoader.addDependencies(cloudModuleFileContent.dependencies)
            dependencyLoader.installDependencies()
            val cloudModule = this.loadModuleClass(file, cloudModuleFileContent.mainClass)
            cloudModule.onEnable()
            return cloudModule
        } catch (ex: Exception) {
           throw CloudModuleLoadException("Error while loading module: ${file.path}", ex)
        }
    }

    private fun addModuleAsDependency(file: File) {
        val urlClassLoader = ClassLoader.getSystemClassLoader() as URLClassLoader
        val method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
        method.isAccessible = true
        method.invoke(urlClassLoader, file.toURI().toURL())
    }

    private fun loadModuleClass(file: File, mainClass: String): ICloudModule {
        try {
            return ExtensionLoader<ICloudModule>().loadClass(file, mainClass, ICloudModule::class.java)
        } catch (ex: Exception) {
            throw CloudModuleLoadException("Error while loading module ${file.path}.", ex)
        }
    }

    fun loadModuleFileContent(file: File, moduleFileName: String): CloudModuleFileContent {
        require(file.exists()) { "Specified file to load module from does not exist: ${file.path}" }
        val fileStream = ResourceFinder.findResource(file, moduleFileName)
                ?: throw CloudModuleLoadException("Error while loading module ${file.path}: No '$moduleFileName.json' found.")
        val jsonData = JsonData.fromInputStream(fileStream)
        return jsonData.getObjectOrNull(CloudModuleFileContent::class.java)
                ?: throw CloudModuleLoadException("Error while loading module ${file.path}: Empty '$moduleFileName.json'.")
    }

}