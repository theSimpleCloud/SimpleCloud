package eu.thesimplecloud.base.manager.filehandler

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.config.IFileHandler
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.template.ITemplateGroup
import eu.thesimplecloud.lib.template.impl.DefaultTemplateGroup
import java.io.File

class TemplatesFileHandler : IFileHandler<ITemplateGroup> {

    val directory = File(DirectoryPaths.paths.templateFilesPath)

    override fun save(value: ITemplateGroup) {
        JsonData.fromObject(value).saveAsFile(getFile(value))
    }

    override fun delete(value: ITemplateGroup) {
        getFile(value).delete()
    }

    override fun loadAll(): Set<ITemplateGroup> = directory.listFiles().mapNotNull { JsonData.fromJsonFile(it).getObject(DefaultTemplateGroup::class.java) }.toSet()

    fun getFile(templateGroup: ITemplateGroup): File = File(directory, templateGroup.getName() + ".json")
}