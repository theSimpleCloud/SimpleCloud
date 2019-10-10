package eu.thesimplecloud.base.manager.config

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.config.IConfigLoader
import eu.thesimplecloud.lib.config.IFileHandler
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.template.ITemplate
import java.io.File

class TemplatesConfigLoader : IConfigLoader<TemplatesConfig> {

    val file = File(DirectoryPaths.paths.storagePath + "templates.json")

    override fun loadConfig(): TemplatesConfig = JsonData.fromJsonFile(file).getObject(TemplatesConfig::class.java) ?: TemplatesConfig(emptyList())

    override fun saveConfig(value: TemplatesConfig) {
        JsonData.fromObject(value).saveAsFile(file)
    }

    override fun doesConfigFileExist(): Boolean = file.exists()


}