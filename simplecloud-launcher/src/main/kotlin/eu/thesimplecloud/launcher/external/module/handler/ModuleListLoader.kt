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

import eu.thesimplecloud.launcher.exception.module.ModuleLoadException
import eu.thesimplecloud.launcher.extension.hasNulls
import eu.thesimplecloud.launcher.external.module.LoadedModule
import eu.thesimplecloud.launcher.external.module.LoadedModuleFileContent
import eu.thesimplecloud.launcher.external.module.ModuleFileContent
import java.net.URL

/**
 * Created by IntelliJ IDEA.
 * Date: 26.11.2020
 * Time: 15:48
 * @author Frederick Baier
 */
class ModuleListLoader(
    private val modulesToLoad: List<LoadedModuleFileContent>,
    private val loadedModuleList: List<LoadedModule>,
    private val classLoaderFunction: (Array<URL>, String) -> ClassLoader,
    private val handleLoadError: (Throwable) -> Unit = { throw it }
) {

    private val loadedModules: MutableList<LoadedModule> = ArrayList()

    fun loadModules(): List<LoadedModule> {
        this.modulesToLoad.forEach {
            loadSingleModuleCatching(it)
        }
        return this.loadedModules
    }

    private fun loadSingleModuleCatching(module: LoadedModuleFileContent) {
        try {
            loadSingleModule(module)
        } catch (ex: Exception) {
            this.handleLoadError(ModuleLoadException(module.content.name, ex))
        }
    }

    private fun loadSingleModule(module: LoadedModuleFileContent) {
        checkRequirements(module)
        val moduleDependencies = getAllHardAndAvailableSoftDependencies(module)
        moduleDependencies.forEach { loadSingleModule(it) }
        loadModuleSafe(module)
    }

    private fun checkRequirements(module: LoadedModuleFileContent) {
        if (hasMissingDependencies(module)) {
            throw ModuleLoadException("Missing dependency detected in module: ${module.content.name}")
        }
        if (hasRecursiveDependencies(module)) {
            throw ModuleLoadException("Recursive dependency detected in module: ${module.content.name}")
        }
    }

    private fun loadModuleSafe(module: LoadedModuleFileContent) {
        if (isModuleLoaded(module))
            return
        val loadedModule = UnsafeModuleLoader(classLoaderFunction).loadModule(module)
        this.loadedModules.add(loadedModule)
    }

    private fun isModuleLoaded(module: LoadedModuleFileContent): Boolean {
        return isModuleAlreadyLoaded(module) || wasModuleLoadedBefore(module)
    }

    private fun isModuleAlreadyLoaded(module: LoadedModuleFileContent): Boolean {
        return this.loadedModules.map { it.fileContent.name }.contains(module.content.name)
    }

    private fun wasModuleLoadedBefore(module: LoadedModuleFileContent): Boolean {
        return this.loadedModuleList.map { it.fileContent.name }.contains(module.content.name)
    }

    private fun hasMissingDependencies(module: LoadedModuleFileContent): Boolean {
        val allHardDependencies = getAllHardDependencies(module.content)
        return allHardDependencies.hasNulls()
    }

    private fun hasRecursiveDependencies(module: LoadedModuleFileContent): Boolean {
        return RecursiveDependencyChecker(
            module,
            this::getAllHardAndAvailableSoftDependencies
        ).hasRecursiveDependencies()
    }

    private fun getAllHardAndAvailableSoftDependencies(module: LoadedModuleFileContent): Set<LoadedModuleFileContent> {
        val hardDependencies = getAllHardDependencies(module.content).requireNoNulls()
        val availableSoftDependencies = getAllSoftDependencies(module.content).filterNotNull()
        return hardDependencies.union(availableSoftDependencies)
    }

    private fun getAllHardDependencies(content: ModuleFileContent): List<LoadedModuleFileContent?> {
        return content.depend.map { findModuleFileContentByName(it) }
    }

    private fun getAllSoftDependencies(content: ModuleFileContent): List<LoadedModuleFileContent?> {
        return content.softDepend.map { findModuleFileContentByName(it) }
    }

    private fun findModuleFileContentByName(name: String): LoadedModuleFileContent? {
        return this.modulesToLoad.firstOrNull { it.content.name == name }
    }

}