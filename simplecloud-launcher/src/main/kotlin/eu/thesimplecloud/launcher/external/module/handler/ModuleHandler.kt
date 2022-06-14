/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.launcher.external.module.handler

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.launcher.exception.module.ModuleLoadException
import eu.thesimplecloud.launcher.external.module.LoadedModule
import eu.thesimplecloud.launcher.external.module.LoadedModuleFileContent
import eu.thesimplecloud.launcher.external.module.ModuleClassLoader
import eu.thesimplecloud.launcher.external.module.ModuleFileContent
import eu.thesimplecloud.launcher.external.module.update.UpdaterFileContent
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.concurrent.CopyOnWriteArrayList
import java.util.jar.JarEntry
import java.util.jar.JarFile

class ModuleHandler(
    private val parentClassLoader: ClassLoader = ClassLoader.getSystemClassLoader(),
    private val currentLanguage: String = "en",
    private val modulesWithPermissionToUpdate: List<String> = emptyList(),
    private val checkForUpdates: Boolean = false,
    private val handleLoadError: (Throwable) -> Unit = { throw it }
) : IModuleHandler {

    private val loadedModules: MutableList<LoadedModule> = CopyOnWriteArrayList()

    private var createModuleClassLoaderFunction: (Array<URL>, String) -> URLClassLoader = { args, name ->
        ModuleClassLoader(args, parentClassLoader, name, this)
    }


    override fun loadAllUnloadedModules() {
        loadModuleListFromFiles(getAllModuleJarFiles())
    }

    override fun getLoadedModuleByName(name: String): LoadedModule? {
        return this.loadedModules.firstOrNull { it.getName() == name }
    }

    override fun getLoadedModuleByCloudModule(cloudModule: ICloudModule): LoadedModule? {
        return this.loadedModules.firstOrNull { it.cloudModule == cloudModule }
    }

    override fun unloadModule(cloudModule: ICloudModule) {
        val loadedModule = getLoadedModuleByCloudModule(cloudModule)
            ?: throw IllegalArgumentException("Specified cloud module is not registered")
        unloadModule(loadedModule)
    }

    override fun unloadAllModules() {
        this.loadedModules.forEach { unloadModule(it) }
    }

    override fun unloadAllReloadableModules() {
        this.loadedModules.filter { it.cloudModule.isReloadable() }.forEach { unloadModule(it) }
    }

    override fun getLoadedModules(): List<LoadedModule> {
        return this.loadedModules
    }

    private fun unloadModule(loadedModule: LoadedModule) {
        UnsafeModuleUnloader(loadedModule).unload()
        this.loadedModules.remove(loadedModule)
    }

    override fun findModuleClass(name: String): Class<*> {
        val mapNotNull = this.loadedModules.mapNotNull {
            //Launcher.instance.logger.info("searching class $name in ${it.getName()}")
            runCatching {
                (it.moduleClassLoader as ModuleClassLoader).findClass0(name, false)
            }.getOrNull()
        }
        return mapNotNull.firstOrNull() ?: throw ClassNotFoundException(name)
    }

    override fun findModuleOrSystemClass(name: String): Class<*> {
        val clazz = kotlin.runCatching {
            this.findModuleClass(name)
        }.getOrNull()
        if (clazz != null) return clazz

        val classLoader = Launcher.instance.currentClassLoader
        return Class.forName(name, true, classLoader)
    }

    override fun setCreateModuleClassLoader(function: (Array<URL>, String) -> URLClassLoader) {
        this.createModuleClassLoaderFunction = function
    }

    override fun loadModuleFileContent(file: File, moduleFileName: String): ModuleFileContent {
        return this.loadJsonFileInJar(file, moduleFileName)
    }

    override fun loadModuleListFromFiles(files: List<File>): List<LoadedModule> {
        val modules = files.map { loadModuleFileContent(it) }
        val updatedModuleList = modules.map {
            if (this.checkForUpdates) {
                if (checkForUpdate(it)) {
                    return@map loadModuleFileContent(it.file)
                }
            }
            return@map it
        }
        return loadModuleList(updatedModuleList)
    }

    private fun checkForUpdate(loadedModuleFileContent: LoadedModuleFileContent): Boolean {
        return ModuleUpdateInstaller(loadedModuleFileContent).updateIfAvailable()
    }

    override fun loadModuleList(modulesToLoad: List<LoadedModuleFileContent>): List<LoadedModule> {
        val moduleListLoader = ModuleListLoader(
            modulesToLoad,
            this.loadedModules,
            this.createModuleClassLoaderFunction,
            this.handleLoadError
        )
        val newModules = moduleListLoader.loadModules()
        this.loadedModules.addAll(newModules)
        newModules.forEach { registerLanguageFile(it) }
        newModules.forEach { enableModule(it) }
        return newModules
    }

    private fun registerLanguageFile(loadedModule: LoadedModule) {
        ModuleLanguageFileLoader(this.currentLanguage, loadedModule.file, loadedModule.cloudModule)
            .registerLanguageFileIfExist()
    }

    private fun enableModule(module: LoadedModule) {
        try {
            module.cloudModule.onEnable()
        } catch (e: Exception) {
            this.handleLoadError(e)
        }
    }


    private fun loadModuleFileContent(file: File): LoadedModuleFileContent {
        val moduleFileContent = loadModuleFileContent(file, "module.json")
        val updaterFileContent = this.checkPermissionAndLoadUpdaterFile(file, moduleFileContent)
        return LoadedModuleFileContent(
            file,
            moduleFileContent,
            updaterFileContent
        )
    }

    private inline fun <reified T : Any> loadJsonFileInJar(file: File, path: String): T {
        require(file.exists()) { "Specified file to load $path from does not exist: ${file.path}" }
        try {
            val jar = JarFile(file)
            val entry: JarEntry = jar.getJarEntry(path)
                ?: throw ModuleLoadException("${file.path}: No '$path' found.")
            val fileStream = jar.getInputStream(entry)
            val jsonLib = JsonLib.fromInputStream(fileStream)
            jar.close()
            return jsonLib.getObjectOrNull(T::class.java)
                ?: throw ModuleLoadException("${file.path}: Invalid '$path'.")
        } catch (ex: Exception) {
            throw ModuleLoadException(file.path, ex)
        }
    }

    private fun checkPermissionAndLoadUpdaterFile(
        file: File,
        moduleFileContent: ModuleFileContent
    ): UpdaterFileContent? {
        if (!this.modulesWithPermissionToUpdate.contains(moduleFileContent.name)) return null
        return runCatching { loadJsonFileInJar<UpdaterFileContent>(file, "updater.json") }.getOrNull()
    }

    private fun getAllModuleJarFiles(): List<File> {
        return File(DirectoryPaths.paths.modulesPath).listFiles()?.filter { it.name.endsWith(".jar") } ?: emptyList()
    }

    fun getAllCloudModuleFileContents(): List<LoadedModuleFileContent> {
        return getAllModuleJarFiles().map { loadModuleFileContent(it) }
    }

}
