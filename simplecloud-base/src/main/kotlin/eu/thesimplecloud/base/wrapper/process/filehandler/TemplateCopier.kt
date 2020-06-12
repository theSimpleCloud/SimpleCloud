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

package eu.thesimplecloud.base.wrapper.process.filehandler

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.base.core.utils.FileCopier
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.launcher.external.module.ModuleCopyType
import org.apache.commons.io.FileUtils
import java.io.File

class TemplateCopier : ITemplateCopier {

    override fun copyTemplate(cloudService: ICloudService, template: ITemplate) {
        val everyDir = File(DirectoryPaths.paths.templatesPath + "EVERY")
        val everyTypeDir = if (cloudService.getServiceType() == ServiceType.PROXY) File(DirectoryPaths.paths.templatesPath + "EVERY_PROXY") else File(DirectoryPaths.paths.templatesPath + "EVERY_SERVER")
        val templateDirectories = getDirectoriesOfTemplateAndSubTemplates(template)
        val serviceTmpDir = if (cloudService.isStatic()) File(DirectoryPaths.paths.staticPath + cloudService.getName()) else File(DirectoryPaths.paths.tempPath + cloudService.getName())
        val dontCopyTemplates = cloudService.isStatic() && serviceTmpDir.exists()
        if (!dontCopyTemplates) {
            if (everyDir.exists())
                FileUtils.copyDirectory(everyDir, serviceTmpDir)
            if (everyTypeDir.exists())
                FileUtils.copyDirectory(everyTypeDir, serviceTmpDir)
            templateDirectories.filter { it.exists() }.forEach { FileUtils.copyDirectory(it, serviceTmpDir) }
        }
        val cloudPluginFile = File(serviceTmpDir, "/plugins/SimpleCloud-Plugin.jar")
        FileCopier.copyFileOutOfJar(cloudPluginFile, "/SimpleCloud-Plugin.jar")
        generateServiceFile(cloudService, serviceTmpDir)

        val modulesByCopyType = Wrapper.instance.existingModules
                .filter { it.content.moduleCopyType != ModuleCopyType.NONE }.toMutableList()
        if (!cloudService.isLobby())
            modulesByCopyType.removeIf { it.content.moduleCopyType == ModuleCopyType.LOBBY }
        if (!cloudService.isProxy())
            modulesByCopyType.removeIf { it.content.moduleCopyType == ModuleCopyType.PROXY }
        if (cloudService.isProxy())
            modulesByCopyType.removeIf { it.content.moduleCopyType == ModuleCopyType.SERVER }

        val moduleNamesToCopy = getModulesToCopyOfTemplateAndSubTemplates(template)
        val modulesByName = Wrapper.instance.existingModules.filter { moduleNamesToCopy.contains(it.content.name) }

        modulesByCopyType.union(modulesByName).distinctBy { it.content.name }.forEach { FileUtils.copyFile(it.file, File(serviceTmpDir, "/plugins/" + it.file.name)) }

    }

    /*
    override fun loadDependenciesAndInstall(serviceTmpDir: File): DependenciesInformation {
        val pluginsDir = File(serviceTmpDir, "plugins")
        if (!pluginsDir.exists())
            return DependenciesInformation(emptyList(), emptyList())
        val dependenciesInformationList = pluginsDir.listFiles()?.mapNotNull { file -> ResourceFinder.findResource(file, "dependencies.json")?.let { JsonData.fromInputStream(it) } }
                ?.mapNotNull { it.getObjectOrNull(DependenciesInformation::class.java) }
                ?: return DependenciesInformation(emptyList(), emptyList())
        val allRepos = dependenciesInformationList.map { it.repositories }.flatten()
        val allDependencies = dependenciesInformationList.map { it.dependencies }.flatten()
        val dependenciesInformation = DependenciesInformation(allRepos, allDependencies)
        return dependenciesInformation
    }


    private fun installDependencies(dependenciesInformation: DependenciesInformation) {
        Launcher.instance.dependencyLoader.addRepositories(allRepos)
        Launcher.instance.dependencyLoader.addDependencies(allDependencies)
        Launcher.instance.dependencyLoader.installDependencies()
    }

     */

    private fun generateServiceFile(cloudService: ICloudService, serviceTmpDir: File) {
        val communicationClient = Wrapper.instance.communicationClient
        communicationClient as NettyClient
        JsonLib.empty().append("managerHost", communicationClient.getHost())
                .append("managerPort", communicationClient.getPort())
                .append("serviceName", cloudService.getName())
                .saveAsFile(File(serviceTmpDir, "SIMPLE-CLOUD.json"))
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
}