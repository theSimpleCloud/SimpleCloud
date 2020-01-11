package eu.thesimplecloud.base.manager.external

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.external.ResourceFinder
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.exception.CloudModuleException
import eu.thesimplecloud.launcher.external.module.CloudModuleData
import eu.thesimplecloud.launcher.external.module.CloudModuleLoader
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File
import java.lang.IllegalStateException
import java.util.concurrent.CopyOnWriteArrayList

class CloudModuleHandler : ICloudModuleHandler {

    private val cloudModuleLoader = CloudModuleLoader()

    private val loadedModules = CopyOnWriteArrayList<CloudModuleData>()

    fun loadModules() {
        val modulesDir = File(DirectoryPaths.paths.modulesPath)
        for (file in modulesDir.listFiles()) {
            loadModule(file)
        }
    }

    fun unregisterAllModules() {
        this.loadedModules.map { it.cloudModule }.forEach { unloadModule(it) }
    }

    override fun getModuleDataByName(name: String): CloudModuleData? = this.loadedModules.firstOrNull { it.cloudModuleFileContent.name.equals(name, true) }

    override fun getModuleDataByCloudModule(cloudModule: ICloudModule): CloudModuleData? = this.loadedModules.firstOrNull { it.cloudModule == cloudModule }

    override fun unloadModule(cloudModule: ICloudModule) {
        val cloudModuleData = getModuleDataByCloudModule(cloudModule) ?: throw IllegalStateException("Cannot unload unloaded module")
        try {
            cloudModule.onDisable()
        } catch (ex: Exception) {
            Launcher.instance.logger.exception(CloudModuleException("An error occurred while disabling module: ${cloudModuleData.cloudModuleFileContent.name}", ex))
        }
        //unregister all listeners etc.
        CloudAPI.instance.getEventManager().unregisterAllListenersByCloudModule(cloudModule)
        Manager.instance.packetRegistry.unregisterAllPackets(cloudModule)

        ResourceFinder.removeFromClassLoader(cloudModuleData.file)

        this.loadedModules.remove(cloudModuleData)
    }

    override fun loadModule(file: File) {
        val cloudModuleData = try {
            cloudModuleLoader.loadModule(file, "module.json")
        } catch (ex: Exception) {
            Launcher.instance.logger.exception(ex)
            return
        }
        this.loadedModules.add(cloudModuleData)
    }

    override fun getLoadedModules(): List<CloudModuleData> = this.loadedModules

}