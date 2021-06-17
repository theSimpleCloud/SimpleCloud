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

import com.google.common.collect.Maps
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.launcher.application.ApplicationClassLoader
import eu.thesimplecloud.launcher.event.module.ModuleLoadedEvent
import eu.thesimplecloud.launcher.external.module.LoadedModule
import eu.thesimplecloud.launcher.external.module.LoadedModuleFileContent
import eu.thesimplecloud.launcher.external.module.ModuleFileContent
import eu.thesimplecloud.loader.dependency.DependencyLoader
import eu.thesimplecloud.loader.dependency.LauncherDependencyLoader
import eu.thesimplecloud.runner.dependency.AdvancedCloudDependency
import java.io.File
import java.net.URL
import java.net.URLClassLoader

/**
 * Created by IntelliJ IDEA.
 * Date: 26.11.2020
 * Time: 16:56
 * @author Frederick Baier
 */
class UnsafeModuleLoader(
    private val classLoaderFunction: (Array<URL>, String) -> ClassLoader
) {

    fun loadModule(loadedModuleFileContent: LoadedModuleFileContent): LoadedModule {
        val moduleFile = loadedModuleFileContent.file
        val content = loadedModuleFileContent.content
        val loadedDependencyFiles = getRequiredDependencies(content)
        val loadedDependencyURLs = loadedDependencyFiles.map { it.toURI().toURL() }.toTypedArray()
        val classLoader = this.classLoaderFunction(arrayOf(moduleFile.toURI().toURL(), *loadedDependencyURLs), content.name)
        val cloudModule = this.loadModuleClassInstance(classLoader, content.mainClass)
        val loadedModule =
            LoadedModule(cloudModule, moduleFile, content, loadedModuleFileContent.updaterFileContent, classLoader)
        CloudAPI.instance.getEventManager().call(ModuleLoadedEvent(loadedModule))

        return loadedModule
    }

    private fun loadModuleClassInstance(classLoader: ClassLoader, mainClassName: String): ICloudModule {
        val mainClass = loadModuleClass(classLoader, mainClassName)
        val constructor = mainClass.getConstructor()
        return constructor.newInstance()
    }

    private fun loadModuleClass(classLoader: ClassLoader, mainClassName: String): Class<out ICloudModule> {
        val mainClass = classLoader.loadClass(mainClassName)
        return mainClass.asSubclass(ICloudModule::class.java)
    }

    private fun getRequiredDependencies(moduleFileContent: ModuleFileContent): Set<File> {
        val dependencyLoader = DependencyLoader.INSTANCE
        val launcherDependencies = moduleFileContent.dependencies
            .map { AdvancedCloudDependency(it.groupId, it.artifactId, it.version) }
        return dependencyLoader.loadDependencies(
            moduleFileContent.repositories,
            launcherDependencies
        )
    }

}