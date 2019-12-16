package eu.thesimplecloud.base.manager.config

import eu.thesimplecloud.base.manager.mongo.MongoConnectionInformation
import eu.thesimplecloud.lib.config.AbstractJsonDataConfigLoader
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import java.io.File

class MongoDBConfigLoader : AbstractJsonDataConfigLoader<MongoConnectionInformation>(
        MongoConnectionInformation::class.java,
        File(DirectoryPaths.paths.storagePath + "templates.json"),
        { MongoConnectionInformation("127.0.0.1", 27017, "cloud", "simplecloud", "cloudpassword") }
) {
}