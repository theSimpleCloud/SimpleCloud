package eu.thesimplecloud.launcher.exception.module

import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.launcher.external.module.LoadedModule
import eu.thesimplecloud.launcher.external.module.LoadedModuleFileContent
import eu.thesimplecloud.launcher.external.module.ModuleFileContent
import java.io.File
import java.net.URL
import java.net.URLClassLoader

interface IModuleHandler {

    /**
     * Loads only the content of the modules json file.
     */
    fun loadModuleFileContent(file: File, moduleFileName: String = "module.json"): ModuleFileContent

    /**
     * Loads a module
     */
    fun loadModule(loadedModuleFileContent: LoadedModuleFileContent): LoadedModule

    /**
     * Loads the module from the specified [file]
     */
    fun loadModule(file: File, moduleFileName: String = "module.json"): LoadedModule

    /**
     * Loads all unloaded modules form the modules directory.
     */
    fun loadAllUnloadedModules()

    /**
     * Returns the first [LoadedModule] found by the specified [name]
     */
    fun getLoadedModuleByName(name: String): LoadedModule?

    /**
     * Returns the first [LoadedModule] found by the specified [cloudModule]
     */
    fun getLoadedModuleByCloudModule(cloudModule: ICloudModule): LoadedModule?

    /**
     * Unloaded the specified [cloudModule]
     */
    fun unloadModule(cloudModule: ICloudModule)

    /**
     * Unloads all modules
     */
    fun unloadAllModules()

    /**
     * Unloads all reloadable modules
     * @see [ICloudModule.isReloadable]
     */
    fun unloadAllReloadableModules()

    /**
     * Returns a list containing all loaded modules.
     */
    fun getLoadedModules(): List<LoadedModule>

    /**
     * Searches the class with the given name.
     * This methods searches also in classloaders of all modules.
     */
    @Throws(ClassNotFoundException::class)
    fun findModuleClass(name: String): Class<*>

    /**
     * Sets the function to create module class loaders.
     */
    fun setCreateModuleClassLoader(function: (Array<URL>, String) -> URLClassLoader)
}