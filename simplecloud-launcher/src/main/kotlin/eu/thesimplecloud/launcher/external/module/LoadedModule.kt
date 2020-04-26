package eu.thesimplecloud.launcher.external.module

import eu.thesimplecloud.api.external.ICloudModule
import java.io.File

class LoadedModule(
        val cloudModule: ICloudModule,
        val file: File,
        val fileContent: ModuleFileContent,
        val moduleClassLoader: ClassLoader
) {

    fun getLoadedModuleFileContent(): LoadedModuleFileContent {
        return LoadedModuleFileContent(file, fileContent)
    }

}