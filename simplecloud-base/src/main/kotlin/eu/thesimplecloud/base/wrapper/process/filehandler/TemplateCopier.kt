package eu.thesimplecloud.base.wrapper.process.filehandler

import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.base.core.utils.FileCopier
import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceType
import eu.thesimplecloud.lib.template.ITemplate
import org.apache.commons.io.FileUtils
import java.io.File

class TemplateCopier : ITemplateCopier {

    override fun copyTemplate(cloudService: ICloudService, template: ITemplate) {
        val everyDir = File(DirectoryPaths.paths.templatesPath + "EVERY")
        val everyTypeDir = if (cloudService.getServiceType() == ServiceType.PROXY) File(DirectoryPaths.paths.templatesPath + "EVERY_PROXY") else File(DirectoryPaths.paths.templatesPath + "EVERY_SERVER")
        val templateDirectories = getDirectoriesOfTemplateAndSubTemplates(template)
        val serviceTmpDir = if (cloudService.isStatic()) File(DirectoryPaths.paths.staticPath + cloudService.getName()) else File(DirectoryPaths.paths.tempPath + cloudService.getName())
        FileUtils.copyDirectory(everyDir, serviceTmpDir)
        FileUtils.copyDirectory(everyTypeDir, serviceTmpDir)
        templateDirectories.forEach { FileUtils.copyDirectory(it, serviceTmpDir) }
        val cloudPluginFile = File(serviceTmpDir, "/plugins/SimpleCloud-Plugin.jar")
        FileCopier.copyFileOutOfJar(cloudPluginFile, "/SimpleCloud-Plugin.jar")
        generateServiceFile(cloudService, serviceTmpDir)
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
        JsonData().append("managerHost", communicationClient.getHost())
                .append("managerPort", communicationClient.port)
                .append("serviceName", cloudService.getName())
                .saveAsFile(File(serviceTmpDir, "SIMPLE-CLOUD.json"))
    }

    fun getDirectoriesOfTemplateAndSubTemplates(template: ITemplate): Set<File> {
        val set = HashSet<File>()
        for (templateName in template.getInheritedTemplateNames()) {
            val subTemplate = CloudLib.instance.getTemplateManager().getTemplate(templateName)
            subTemplate?.let { set.addAll(getDirectoriesOfTemplateAndSubTemplates(it)) }
        }
        set.add(template.getDirectory())
        return set
    }
}