package eu.thesimplecloud.base.manager.external

import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.launcher.external.module.CloudModuleData
import java.io.File

interface ICloudModuleHandler {

    /**
     * Returns the first [CloudModuleData] found by the specified [name]
     */
    fun getModuleDataByName(name: String): CloudModuleData?

    /**
     * Returns the first [CloudModuleData] found by the specified [cloudModule]
     */
    fun getModuleDataByCloudModule(cloudModule: ICloudModule): CloudModuleData?

    /**
     * Unloaded the specified [cloudModule]
     */
    fun unloadModule(cloudModule: ICloudModule)

    /**
     * Loads the cloud module from the specified [file]
     */
    fun loadModule(file: File)

    /**
     * Returns a list containing all loaded modules.
     */
    fun getLoadedModules(): List<CloudModuleData>

}