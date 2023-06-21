package eu.thesimplecloud.launcher.external

import eu.thesimplecloud.EmptyMockCloudAPIImpl
import eu.thesimplecloud.api.eventapi.BasicEventManager
import eu.thesimplecloud.launcher.exception.module.ModuleLoadException
import eu.thesimplecloud.launcher.external.module.*
import eu.thesimplecloud.launcher.external.module.handler.ModuleListLoader
import eu.thesimplecloud.launcher.external.module.handler.UnsafeModuleLoader
import io.mockk.*
import org.apache.commons.lang3.RandomStringUtils
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Created by IntelliJ IDEA.T
 * Date: 28.11.2020
 * Time: 11:37
 * @author Frederick Baier
 */
class ModuleListLoaderTest {

    private lateinit var moduleHandler: ModuleListLoader

    @Before
    fun setUp() {
        val eventManager = spyk<BasicEventManager>()
        EmptyMockCloudAPIImpl(eventManager)
        every { eventManager.call(any(), any()) } returns Unit
        mockkConstructor(UnsafeModuleLoader::class)
        mockkConstructor(LoadedModule::class)
    }

    @Test
    fun test() {
        val loadedModuleFileContent = generateLoadedModuleFileContent("Test")
        val modulesToLoad = listOf(loadedModuleFileContent)
        moduleHandler = ModuleListLoader(
            modulesToLoad,
            emptyList(),
            { urls, name -> ModuleClassLoader(urls, ClassLoader.getSystemClassLoader(), name, null) })
        moduleHandler.loadModules()

        verify { anyConstructed<UnsafeModuleLoader>().loadModule(loadedModuleFileContent) }
    }

    @Test
    fun testSkipSoftDependIfNotAvailable() {
        val loadedModuleFileContent = generateLoadedModuleFileContent("Test", emptyList(), listOf("Test2"))
        val modulesToLoad = listOf(loadedModuleFileContent)
        moduleHandler = ModuleListLoader(
            modulesToLoad,
            emptyList(),
            { urls, name -> ModuleClassLoader(urls, ClassLoader.getSystemClassLoader(), name, null) })
        moduleHandler.loadModules()

        verify { anyConstructed<UnsafeModuleLoader>().loadModule(loadedModuleFileContent) }
    }

    @Test
    fun testDependLoad() {
        val content1 = generateLoadedModuleFileContent("Test2", listOf("Test1"))
        val loadedModule = mockk<LoadedModule>()
        every { anyConstructed<UnsafeModuleLoader>().loadModule(any()) }.returns(loadedModule)
        val content2 = generateLoadedModuleFileContent("Test1")
        every { loadedModule.fileContent } returns content2.content
        val modulesToLoad = listOf(content1, content2)
        moduleHandler = ModuleListLoader(
            modulesToLoad,
            emptyList(),
            { urls, name -> ModuleClassLoader(urls, ClassLoader.getSystemClassLoader(), name, null) })
        moduleHandler.loadModules()

        verify { anyConstructed<UnsafeModuleLoader>().loadModule(content1) }
        verify { anyConstructed<UnsafeModuleLoader>().loadModule(content2) }
    }

    @Test
    fun testSoftDependLoad() {
        val content1 = generateLoadedModuleFileContent("Test2", emptyList(), listOf("Test1"))
        val loadedModule = mockk<LoadedModule>()
        every { anyConstructed<UnsafeModuleLoader>().loadModule(any()) }.returns(loadedModule)
        val content2 = generateLoadedModuleFileContent("Test1")
        every { loadedModule.fileContent } returns content2.content
        val modulesToLoad = listOf(content1, content2)
        moduleHandler = ModuleListLoader(
            modulesToLoad,
            emptyList(),
            { urls, name -> ModuleClassLoader(urls, ClassLoader.getSystemClassLoader(), name, null) })
        moduleHandler.loadModules()

        verify { anyConstructed<UnsafeModuleLoader>().loadModule(content1) }
        verify { anyConstructed<UnsafeModuleLoader>().loadModule(content2) }
    }

    @Test(expected = ModuleLoadException::class)
    fun testMissingDependency() {
        val loadedModuleFileContent = generateLoadedModuleFileContent("Test", listOf("Test2"))
        val modulesToLoad = listOf(loadedModuleFileContent)
        moduleHandler = ModuleListLoader(
            modulesToLoad,
            emptyList(),
            { urls, name -> ModuleClassLoader(urls, ClassLoader.getSystemClassLoader(), name, null) })
        moduleHandler.loadModules()
    }

    @Test(expected = ModuleLoadException::class)
    fun testRecursiveDependency() {
        val content1 = generateLoadedModuleFileContent("Test2", listOf("Test1"))
        val loadedModule = mockk<LoadedModule>()
        every { anyConstructed<UnsafeModuleLoader>().loadModule(any()) }.returns(loadedModule)
        val content2 = generateLoadedModuleFileContent("Test1", listOf("Test2"))
        every { loadedModule.fileContent } returns content2.content
        val modulesToLoad = listOf(content1, content2)
        moduleHandler = ModuleListLoader(
            modulesToLoad,
            emptyList(),
            { urls, name -> ModuleClassLoader(urls, ClassLoader.getSystemClassLoader(), name, null) })
        moduleHandler.loadModules()
    }


    @Test(expected = ModuleLoadException::class)
    fun testRecursiveDependencySoftDepend() {
        val content1 = generateLoadedModuleFileContent("Test2", listOf("Test1"))
        val loadedModule = mockk<LoadedModule>()
        every { anyConstructed<UnsafeModuleLoader>().loadModule(any()) }.returns(loadedModule)
        val content2 = generateLoadedModuleFileContent("Test1", emptyList(), listOf("Test2"))
        every { loadedModule.fileContent } returns content2.content
        val modulesToLoad = listOf(content1, content2)
        moduleHandler = ModuleListLoader(
            modulesToLoad,
            emptyList(),
            { urls, name -> ModuleClassLoader(urls, ClassLoader.getSystemClassLoader(), name, null) })
        moduleHandler.loadModules()
    }

    private fun generateLoadedModuleFileContent(
        name: String,
        moduleDepends: List<String> = emptyList(),
        moduleSoftDepends: List<String> = emptyList()
    ): LoadedModuleFileContent {
        val fileContent = ModuleFileContent(
            name,
            RandomStringUtils.randomAlphabetic(20),
            "eu.thesimplecloud.mockmodule.ModuleMain",
            ModuleCopyType.NONE,
            emptyList(),
            emptyList(),
            moduleDepends,
            moduleSoftDepends
        )
        return LoadedModuleFileContent(File(RandomStringUtils.randomAlphabetic(20) + ".jar"), fileContent, null)
    }

}