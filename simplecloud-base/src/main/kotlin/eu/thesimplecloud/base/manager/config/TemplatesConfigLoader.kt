package eu.thesimplecloud.base.manager.config

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.config.AbstractJsonDataConfigLoader
import eu.thesimplecloud.lib.config.IConfigLoader
import eu.thesimplecloud.lib.config.IFileHandler
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.template.ITemplate
import java.io.File

class TemplatesConfigLoader : AbstractJsonDataConfigLoader<TemplatesConfig>(
        TemplatesConfig::class.java,
        File(DirectoryPaths.paths.storagePath + "templates.json"),
        { TemplatesConfig(HashSet()) }
)