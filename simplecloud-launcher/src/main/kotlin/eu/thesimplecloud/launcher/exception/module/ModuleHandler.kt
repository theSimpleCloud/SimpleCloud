package eu.thesimplecloud.launcher.exception.module

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.dependency.DependencyLoader
import eu.thesimplecloud.launcher.event.module.ModuleLoadedEvent
import eu.thesimplecloud.launcher.event.module.ModuleUnloadedEvent
import eu.thesimplecloud.launcher.exception.module.exception.ModuleLoadException
import eu.thesimplecloud.launcher.external.module.LoadedModule
import eu.thesimplecloud.launcher.external.module.LoadedModuleFileContent
import eu.thesimplecloud.launcher.external.module.ModuleClassLoader
import eu.thesimplecloud.launcher.external.module.ModuleFileContent
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.concurrent.CopyOnWriteArrayList
import java.util.jar.JarEntry
import java.util.jar.JarFile

open class ModuleHandler(
        private val handleException: (Throwable) -> Unit = { throw it }
) : IModuleHandler {

    private val loadedModuleFileContents: MutableSet<LoadedModuleFileContent> = mutableSetOf()

    private val loadedModules = CopyOnWriteArrayList<LoadedModule>()


    private var createModuleClassLoader: (Array<URL>, String) -> URLClassLoader = { urls, name -> ModuleClassLoader(urls, ClassLoader.getSystemClassLoader(), name, this) }

    override fun setCreateModuleClassLoader(function: (Array<URL>, String) -> URLClassLoader) {
        this.createModuleClassLoader = function
    }

    override fun loadModuleFileContent(file: File, moduleFileName: String): ModuleFileContent {
        require(file.exists()) { "Specified file to load module from does not exist: ${file.path}" }
        try {
            val jar = JarFile(file)
            val entry: JarEntry = jar.getJarEntry(moduleFileName)
                    ?: throw ModuleLoadException("${file.path}: No '$moduleFileName.json' found.")
            val fileStream = jar.getInputStream(entry)
            val jsonData = JsonData.fromInputStream(fileStream)
            return jsonData.getObjectOrNull(ModuleFileContent::class.java)
                    ?: throw ModuleLoadException("${file.path}: Invalid '$moduleFileName.json'.")
        } catch (ex: Exception) {
            throw ModuleLoadException(file.path, ex)
        }
    }

    @Synchronized
    override fun loadModule(loadedModuleFileContent: LoadedModuleFileContent): LoadedModule {
        if (this.loadedModules.any { it.file.path == loadedModuleFileContent.file.path })
            throw IllegalStateException("Module ${loadedModuleFileContent.content.name} is already loaded")
        if (this.loadedModules.any { it.fileContent.name == loadedModuleFileContent.content.name })
            throw IllegalStateException("Duplicate module name ${loadedModuleFileContent.content.name}")
        this.loadedModuleFileContents.add(loadedModuleFileContent)
        checkForSelfDepend(loadedModuleFileContent)
        checkForMissingDependencies(loadedModuleFileContent, getAllLoadedModuleNames())
        checkForRecursiveDependencies(loadedModuleFileContent)
        val (file, content) = loadedModuleFileContent
        try {
            installRequiredDependencies(content)
            val classLoader = this.createModuleClassLoader(arrayOf(file.toURI().toURL()), loadedModuleFileContent.content.name)
            val cloudModule = this.loadModuleClassInstance(classLoader, content.mainClass)
            cloudModule.onEnable()
            val loadedModule = LoadedModule(cloudModule, file, content, classLoader)
            this.loadedModules.add(loadedModule)
            CloudAPI.instance.getEventManager().call(ModuleLoadedEvent(loadedModule))
            return loadedModule
        } catch (ex: Exception) {
            throw ModuleLoadException(file.path, ex)
        }
    }

    override fun loadModule(file: File, moduleFileName: String): LoadedModule {
        if (getLoadedModuleByFile(file) != null)
            throw IllegalStateException("Module ${file.path} is already loaded.")
        val content = loadModuleFileContent(file, moduleFileName)
        val loadedModuleFileContent = LoadedModuleFileContent(file, content)
        return loadModule(loadedModuleFileContent)
    }

    private fun getLoadedModuleByFile(file: File): LoadedModule? {
        return this.loadedModules.firstOrNull { it.file.path == file.path }
    }

    override fun loadAllUnloadedModules() {
        this.loadedModuleFileContents.addAll(getAllCloudModuleFileContents().toMutableSet())
        val allModuleNames: List<String> = getAllLoadedModuleNames()
        checkForMissingDependencies(allModuleNames)
        checkForRecursiveDependencies()
        val modulesWithUnknownDependencies = this.loadedModuleFileContents
                .filter { hasUnknownDependencies(it, allModuleNames) }
        this.loadedModuleFileContents.removeAll(modulesWithUnknownDependencies)
        val validModuleFileContents = this.loadedModuleFileContents
                .filter { !hasRecursiveDependencies(it) }
        val loadedModuleContentsInOrder = getModuleLoadOrder(validModuleFileContents)
        loadedModuleContentsInOrder.forEach { loadModuleSafe(it) }
    }

    private fun getAllLoadedModuleNames() = this.loadedModuleFileContents.map { it.content.name }

    private fun loadModuleSafe(loadedModuleFileContent: LoadedModuleFileContent) {
        try {
            loadModule(loadedModuleFileContent)
        } catch (ex: IllegalStateException) {
            if (ex.message?.contains("already loaded") == true) {
                //ignore
                return
            }
            throw ex
        }
    }

    private fun installRequiredDependencies(cloudModuleFileContent: ModuleFileContent) {
        val dependencyLoader = DependencyLoader.INSTANCE
        dependencyLoader.addRepositories(cloudModuleFileContent.repositories)
        dependencyLoader.addDependencies(cloudModuleFileContent.dependencies)
        dependencyLoader.installDependencies()
    }

    private fun loadModuleClassInstance(classLoader: ClassLoader, mainClassName: String): ICloudModule {
        val mainClass = loadModuleClass(classLoader, mainClassName)
        val constructor = mainClass.getConstructor()
        return constructor.newInstance()
    }

    fun loadModuleClass(classLoader: ClassLoader, mainClassName: String): Class<out ICloudModule> {
        val mainClass = classLoader.loadClass(mainClassName)
        return mainClass.asSubclass(ICloudModule::class.java)
    }

    private fun getModuleLoadOrder(fileContents: List<LoadedModuleFileContent>): List<LoadedModuleFileContent> {
        return fileContents.map { addModuleFileContentsOfDependenciesAndSubDependencies(it) }.flatten()
    }

    private fun addModuleFileContentsOfDependenciesAndSubDependencies(moduleFileContent: LoadedModuleFileContent, list: MutableList<LoadedModuleFileContent> = ArrayList()): MutableList<LoadedModuleFileContent> {
        list.add(moduleFileContent)
        val dependenciesFileContents = moduleFileContent.content.depend.map { getLoadedModuleFileContentByName(it) }
        if (dependenciesFileContents.any { it == null }) {
            val nullDependencies = moduleFileContent.content.depend.filter { getLoadedModuleFileContentByName(it) == null }
            throw IllegalStateException("Failed to load module ${moduleFileContent.content.name}: Unable to find dependencies $nullDependencies")
        }
        val noNullList = dependenciesFileContents.requireNoNulls()
        noNullList.forEach {
            if (!list.contains(it)) {
                addModuleFileContentsOfDependenciesAndSubDependencies(it, list)
            }
        }
        return list
    }

    private fun checkForSelfDepend(loadedModuleFileContent: LoadedModuleFileContent) {
        if (loadedModuleFileContent.content.dependsFrom(loadedModuleFileContent.content))
            throw ModuleLoadException("${loadedModuleFileContent.content.name} self depend")
    }

    private fun checkForRecursiveDependencies() {
        for (moduleFileContent in this.loadedModuleFileContents) {
            try {
                checkForRecursiveDependencies(moduleFileContent)
            } catch (ex: Exception) {
                handleException(ex)
            }
        }
    }

    private fun checkForRecursiveDependencies(moduleFileContent: LoadedModuleFileContent) {
        if (hasRecursiveDependencies(moduleFileContent))
            throw ModuleLoadException("${moduleFileContent.content.name} recursive dependency detected: ${getRecursiveDependencies(moduleFileContent).joinToString { it.content.name }}")
    }

    private fun checkForMissingDependencies(allModuleNames: List<String>) {
        for (moduleFileContent in this.loadedModuleFileContents) {
            try {
                checkForMissingDependencies(moduleFileContent, allModuleNames)
            } catch (ex: ModuleLoadException) {
                handleException(ex)
            }
        }
    }

    private fun checkForMissingDependencies(moduleFileContent: LoadedModuleFileContent, allModuleNames: List<String>) {
        val unknownDependencies = getUnknownDependencies(moduleFileContent, allModuleNames)
        if (unknownDependencies.isNotEmpty())
            throw ModuleLoadException("Failed to load module ${moduleFileContent.content.name}: Module dependencies are missing: ${unknownDependencies.joinToString()}")
    }

    private fun getRecursiveDependencies(moduleFileContent: LoadedModuleFileContent): List<LoadedModuleFileContent> {
        val fileContentsOfDependencies = addModuleFileContentsOfDependenciesAndSubDependencies(moduleFileContent)
        return fileContentsOfDependencies.filter { it.content.dependsFrom(moduleFileContent.content) }
    }

    private fun hasRecursiveDependencies(moduleFileContent: LoadedModuleFileContent) =
            getRecursiveDependencies(moduleFileContent).isNotEmpty()

    private fun getUnknownDependencies(fileContent: LoadedModuleFileContent, moduleNames: List<String>) =
            fileContent.content.depend.filter { !moduleNames.contains(it) }

    private fun hasUnknownDependencies(fileContent: LoadedModuleFileContent, moduleNames: List<String>) =
            getUnknownDependencies(fileContent, moduleNames).isNotEmpty()

    open fun getAllCloudModuleFileContents(): List<LoadedModuleFileContent> {
        return getAllModuleJarFiles().map { LoadedModuleFileContent(it, this.loadModuleFileContent(it, "module.json")) }
    }

    private fun getAllModuleJarFiles(): List<File> {
        return File(DirectoryPaths.paths.modulesPath).listFiles()?.filter { it.name.endsWith(".jar") } ?: emptyList()
    }

    private fun getLoadedModuleFileContentByName(name: String) =
            this.loadedModuleFileContents.firstOrNull { it.content.name == name }


    override fun getLoadedModuleByName(name: String): LoadedModule? {
        return this.loadedModules.firstOrNull { it.fileContent.name == name }
    }

    override fun getLoadedModuleByCloudModule(cloudModule: ICloudModule): LoadedModule? {
        return this.loadedModules.firstOrNull { it.cloudModule == cloudModule }
    }

    override fun unloadAllModules() {
        this.loadedModules.forEach {
            unloadModule(it.cloudModule)
        }
    }

    override fun unloadAllReloadableModules() {
        this.loadedModules.filter { it.cloudModule.isReloadable() }.forEach {
            unloadModule(it.cloudModule)
        }
    }

    override fun unloadModule(cloudModule: ICloudModule) {
        val loadedModule = getLoadedModuleByCloudModule(cloudModule)
                ?: throw IllegalStateException("Cannot unload unloaded module")
        try {
            cloudModule.onDisable()
        } catch (ex: Exception) {
            handleException(ex)
        }
        //unregister all listeners etc.
        CloudAPI.instance.getEventManager().unregisterAllListenersByCloudModule(cloudModule)
        (loadedModule.moduleClassLoader as ModuleClassLoader).close()

        this.loadedModules.remove(loadedModule)
        CloudAPI.instance.getEventManager().call(ModuleUnloadedEvent(loadedModule))
    }

    override fun getLoadedModules(): List<LoadedModule> = this.loadedModules


    override fun findModuleClass(name: String): Class<*> {
        val mapNotNull = this.loadedModules.mapNotNull {
            runCatching {
                (it.moduleClassLoader as ModuleClassLoader).findClass0(name, false)
            }.getOrNull()
        }
        return mapNotNull.firstOrNull() ?: throw ClassNotFoundException(name)
    }

}
