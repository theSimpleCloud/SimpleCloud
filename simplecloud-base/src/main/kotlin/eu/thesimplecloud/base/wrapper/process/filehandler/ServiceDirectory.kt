/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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

package eu.thesimplecloud.base.wrapper.process.filehandler

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.api.service.version.type.ServiceAPIType
import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.launcher.external.module.LoadedModuleFileContent
import eu.thesimplecloud.launcher.external.module.ModuleCopyType
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.utils.FileCopier
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException

class ServiceDirectory(private val cloudService: ICloudService) {

    private var copiedModulesAsPlugins: List<File> = emptyList()
    val serviceTmpDirectory = getServiceTmpDirectory(cloudService)

    fun copyTemplateFilesAndModules() {
        copyTemplateFiles()
        copyServiceVersionIfNotExist()
        copyModules()
    }

    fun deleteTemporaryModuleFiles() {
        this.copiedModulesAsPlugins.forEach { it.delete() }
    }

    @Throws(IOException::class)
    fun deleteServiceDirectoryUnsafe() {
        FileUtils.deleteDirectory(this.serviceTmpDirectory)
    }

    private fun copyServiceVersionIfNotExist() {
        val expectedExecutableJar = File(this.serviceTmpDirectory, "server.jar")
        if (expectedExecutableJar.exists()) {
            return
        }

        val loadedServiceVersion =
            Wrapper.instance.serviceVersionLoader.loadVersionFile(this.cloudService.getServiceVersion())
        loadedServiceVersion.copyToDirectory(this.serviceTmpDirectory)
        renameExecutableJar(loadedServiceVersion)
    }

    private fun renameExecutableJar(loadedServiceVersion: LoadedServiceVersion) {
        val executableJar = File(this.serviceTmpDirectory, loadedServiceVersion.fileNameToExecute)
        val renamedExecutableJar = File(this.serviceTmpDirectory, "server.jar")
        executableJar.renameTo(renamedExecutableJar)
    }

    private fun copyTemplateFiles() {
        val template = cloudService.getTemplate()
        val everyDir = File(DirectoryPaths.paths.templatesPath + "EVERY")
        val everyTypeDir = if (cloudService.getServiceType() == ServiceType.PROXY)
            File(DirectoryPaths.paths.templatesPath + "EVERY_PROXY")
        else
            File(DirectoryPaths.paths.templatesPath + "EVERY_SERVER")
        val templateDirectories = getDirectoriesOfTemplateAndSubTemplates(template)

        val dontCopyTemplates = cloudService.isStatic() && !cloudService.isForceCopyTemplates() && this.serviceTmpDirectory.exists()
        if (!dontCopyTemplates) {
            if (everyDir.exists())
                FileUtils.copyDirectory(everyDir, this.serviceTmpDirectory)
            if (everyTypeDir.exists())
                FileUtils.copyDirectory(everyTypeDir, this.serviceTmpDirectory)
            templateDirectories.filter { it.exists() }.forEach { FileUtils.copyDirectory(it, this.serviceTmpDirectory) }
        }

        if (cloudService.getServiceType() == ServiceType.PROXY) {
            val destServerIconFile = File(this.serviceTmpDirectory, "/server-icon.png")
            if (!destServerIconFile.exists())
                FileCopier.copyFileOutOfJar(destServerIconFile, "/files/server-icon.png")
        }

        if (cloudService.getServiceVersion().serviceAPIType == ServiceAPIType.MINESTOM) {
            val cloudPluginFile = File(this.serviceTmpDirectory, "/${getPluginDirectoryName()}/SimpleCloud-Extension.jar")
            val version = Launcher.instance.getCurrentVersion().replace("-SNAPSHOT", "")
            File(DirectoryPaths.paths.storagePath + "pluginJars/SimpleCloud-Extension-$version.jar").copyTo(cloudPluginFile, true)
        } else {
            val cloudPluginFile = File(this.serviceTmpDirectory, "/${getPluginDirectoryName()}/SimpleCloud-Plugin.jar")
            val version = Launcher.instance.getCurrentVersion().replace("-SNAPSHOT", "")
            File(DirectoryPaths.paths.storagePath + "pluginJars/SimpleCloud-Plugin-$version.jar").copyTo(cloudPluginFile, true)
        }

        generateServiceFile()
    }

    private fun copyModules() {
        val modulesForService = getModulesForService()
        modulesForService.forEach {
            FileUtils.copyFile(
                it.file,
                File(this.serviceTmpDirectory, "/${getPluginDirectoryName()}/" + it.file.name)
            )
        }
        this.copiedModulesAsPlugins = getModuleFilesInService()
    }

    private fun getServiceTmpDirectory(cloudService: ICloudService): File {
        return if (cloudService.isStatic())
            File(DirectoryPaths.paths.staticPath + cloudService.getName())
        else
            File(DirectoryPaths.paths.tempPath + cloudService.getName())
    }

    private fun getModulesForService(): List<LoadedModuleFileContent> {
        val modulesByCopyType = Wrapper.instance.existingModules
            .filter { it.content.moduleCopyType != ModuleCopyType.NONE }.toMutableList()
        if (!cloudService.isLobby())
            modulesByCopyType.removeIf { it.content.moduleCopyType == ModuleCopyType.LOBBY }
        if (!cloudService.isProxy())
            modulesByCopyType.removeIf { it.content.moduleCopyType == ModuleCopyType.PROXY }
        if (cloudService.isProxy())
            modulesByCopyType.removeIf { it.content.moduleCopyType == ModuleCopyType.SERVER }

        val moduleNamesToCopy = getModulesToCopyOfTemplateAndSubTemplates(this.cloudService.getTemplate())
        val modulesByName = Wrapper.instance.existingModules.filter { moduleNamesToCopy.contains(it.content.name) }
        return modulesByCopyType.union(modulesByName).distinctBy { it.content.name }
    }

    fun getModuleFilesInService(): List<File> {
        val modulesForService = getModulesForService()
        return modulesForService.map { File(this.serviceTmpDirectory, "/${getPluginDirectoryName()}/" + it.file.name) }
    }

    private fun generateServiceFile() {
        val communicationClient = Wrapper.instance.communicationClient
        communicationClient as NettyClient
        JsonLib.empty().append("managerHost", communicationClient.getHost())
            .append("managerPort", communicationClient.getPort())
            .append("serviceName", cloudService.getName())
            .saveAsFile(File(this.serviceTmpDirectory, "SIMPLE-CLOUD.json"))
    }

    private fun getDirectoriesOfTemplateAndSubTemplates(template: ITemplate): Set<File> {
        val set = HashSet<File>()
        for (templateName in template.getInheritedTemplateNames()) {
            val subTemplate = CloudAPI.instance.getTemplateManager().getTemplateByName(templateName)
            subTemplate?.let { set.addAll(getDirectoriesOfTemplateAndSubTemplates(it)) }
        }
        set.add(template.getDirectory())
        return set
    }

    private fun getModulesToCopyOfTemplateAndSubTemplates(template: ITemplate): Set<String> {
        val set = HashSet<String>()
        for (templateName in template.getInheritedTemplateNames()) {
            val subTemplate = CloudAPI.instance.getTemplateManager().getTemplateByName(templateName)
            subTemplate?.let { set.addAll(getModulesToCopyOfTemplateAndSubTemplates(it)) }
        }
        set.addAll(template.getModuleNamesToCopy())
        return set
    }
    
    private fun getPluginDirectoryName(): String {
        if (cloudService.getServiceVersion().serviceAPIType == ServiceAPIType.MINESTOM) {
            return "extensions"
        }
        
        return "plugins"
    }
    
}