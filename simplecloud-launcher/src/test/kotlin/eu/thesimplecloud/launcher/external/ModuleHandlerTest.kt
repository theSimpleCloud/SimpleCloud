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

package eu.thesimplecloud.launcher.external

import eu.thesimplecloud.EmptyMockCloudAPIImpl
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.eventapi.BasicEventManager
import eu.thesimplecloud.launcher.exception.module.ModuleLoadException
import eu.thesimplecloud.launcher.external.module.LoadedModuleFileContent
import eu.thesimplecloud.launcher.external.module.ModuleCopyType
import eu.thesimplecloud.launcher.external.module.ModuleFileContent
import eu.thesimplecloud.launcher.external.module.handler.ModuleHandler
import eu.thesimplecloud.mockmodule.ModuleMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.io.File

class ModuleHandlerTest {

    private var currentModuleCount = 0
    private lateinit var moduleHandler: ModuleHandler

    @Before
    fun before() {
        DirectoryPaths.paths = DirectoryPaths()
        val eventManager = BasicEventManager()
        val cloudAPI = spy(EmptyMockCloudAPIImpl::class.java)
        `when`(cloudAPI.getEventManager()).thenReturn(eventManager)

        this.moduleHandler = spy(ModuleHandler::class.java)
    }

    @Test
    fun module_load_unload_test() {
        val loadedModuleFileContent = constructLoadedModuleAndPrepareForLoad()
        val testModuleFileContent = loadedModuleFileContent.content
        val file = loadedModuleFileContent.file

        val loadedModule = this.moduleHandler.loadModule(file)

        assertEquals(testModuleFileContent, loadedModule.fileContent)
        assertTrue(this.moduleHandler.getLoadedModules().contains(loadedModule))

        //already loaded
        assertThrows(IllegalStateException::class.java) { this.moduleHandler.loadModule(file) }

        this.moduleHandler.unloadModule(loadedModule.cloudModule)

    }

    @Test
    fun module_duplicate_name_test() {
        val loadedModule1 = constructLoadedModuleAndPrepareForLoad()
        val loadedModule2 = LoadedModuleFileContent(File(loadedModule1.file.path + "1"), loadedModule1.content, null)
        moduleHandler.loadModule(loadedModule1)
        assertThrows(IllegalStateException::class.java) { moduleHandler.loadModule(loadedModule2) }
    }

    @Test
    fun missing_dependency_test() {
        val loadedModule = constructLoadedModuleAndPrepareForLoad(listOf("testDependency"))
        assertThrows(ModuleLoadException::class.java) { moduleHandler.loadModule(loadedModule) }
    }

    @Test
    fun self_dependency_test() {
        val loadedModule1 = constructLoadedModuleAndPrepareForLoad()
        val moduleContent1 = loadedModule1.content
        val testModule = LoadedModuleFileContent(loadedModule1.file, ModuleFileContent(moduleContent1.name, moduleContent1.author, moduleContent1.mainClass, moduleContent1.moduleCopyType, moduleContent1.repositories, moduleContent1.dependencies, listOf(moduleContent1.name)), null)
        assertThrows(ModuleLoadException::class.java) { moduleHandler.loadModule(testModule) }
    }

    @Test
    fun recursive_dependency_test() {
        val moduleHandler = spy(ModuleHandler::class.java)
        val loadedModule1 = constructLoadedModuleAndPrepareForLoadAndAddDependency(2)
        val loadedModule2 = constructLoadedModuleAndPrepareForLoadAndAddDependency(-1)
        val loadedModule3 = constructLoadedModuleAndPrepareForLoadAndAddDependency(-1)
        `when`(moduleHandler.getAllCloudModuleFileContents()).thenReturn(listOf(loadedModule1, loadedModule2, loadedModule3))
        assertThrows(ModuleLoadException::class.java) { moduleHandler.loadAllUnloadedModules() }
    }

    @Test
    fun recursive_dependency_test_2() {
        val moduleHandler = spy(ModuleHandler::class.java)
        val loadedModule1 = constructLoadedModuleAndPrepareForLoadAndAddDependency(1)
        val loadedModule2 = constructLoadedModuleAndPrepareForLoadAndAddDependency(-1)
        `when`(moduleHandler.getAllCloudModuleFileContents()).thenReturn(listOf(loadedModule1, loadedModule2))
        assertThrows(ModuleLoadException::class.java) { moduleHandler.loadAllUnloadedModules() }
    }

    @Test
    fun module_load_with_dependencies_test() {
        val moduleHandler = spy(ModuleHandler::class.java)
        val loadedModule1 = constructNewLoadedModuleFileContent()
        val loadedModule2 = constructLoadedModuleAndPrepareForLoadAndAddDependency(-1)
        val loadedModule3 = constructLoadedModuleAndPrepareForLoadAndAddDependency(-1)
        `when`(moduleHandler.getAllCloudModuleFileContents()).thenReturn(listOf(loadedModule1, loadedModule2, loadedModule3))
        moduleHandler.loadAllUnloadedModules()
        assertEquals(3, moduleHandler.getLoadedModules().size)
        assertTrue(moduleHandler.getLoadedModules()[0].cloudModule is ModuleMain)
    }


    @Synchronized
    fun constructNewLoadedModuleFileContent(moduleDependencies: List<String> = emptyList()): LoadedModuleFileContent {
        val moduleName = "test$currentModuleCount"
        val loadedModuleFileContent = LoadedModuleFileContent(
                File("modules/${moduleName}.jar"),
                ModuleFileContent(moduleName, "Wetterbericht", "eu.thesimplecloud.mockmodule.ModuleMain", ModuleCopyType.ALL, emptyList(), emptyList(), moduleDependencies), null)

        currentModuleCount++
        return loadedModuleFileContent
    }

    private fun constructLoadedModuleAndPrepareForLoad(moduleDependencies: List<String> = emptyList()): LoadedModuleFileContent {
        val loadedModuleFileContent = constructNewLoadedModuleFileContent(moduleDependencies)
        doReturn(loadedModuleFileContent.content).`when`(this.moduleHandler).loadModuleFileContent(loadedModuleFileContent.file)
        return loadedModuleFileContent
    }

    private fun constructLoadedModuleAndPrepareForLoadAndAddDependency(relativeDependencyNumber: Int): LoadedModuleFileContent {
        val number = currentModuleCount + relativeDependencyNumber
        return constructLoadedModuleAndPrepareForLoad(listOf("test${number}"))
    }


}