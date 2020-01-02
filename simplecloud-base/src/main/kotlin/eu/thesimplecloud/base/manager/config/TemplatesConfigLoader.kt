package eu.thesimplecloud.base.manager.config

import eu.thesimplecloud.api.config.AbstractJsonDataConfigLoader
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import java.io.File

class TemplatesConfigLoader : AbstractJsonDataConfigLoader<TemplatesConfig>(
        TemplatesConfig::class.java,
        File(DirectoryPaths.paths.storagePath + "templates.json"),
        { TemplatesConfig(HashSet()) }
)