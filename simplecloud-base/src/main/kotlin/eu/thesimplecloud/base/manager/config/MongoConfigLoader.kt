package eu.thesimplecloud.base.manager.config

import eu.thesimplecloud.base.manager.mongo.MongoServerInformation
import eu.thesimplecloud.api.config.AbstractJsonDataConfigLoader
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import java.io.File

class MongoConfigLoader : AbstractJsonDataConfigLoader<MongoConfig>(
        MongoConfig::class.java,
        File(DirectoryPaths.paths.storagePath + "mongo.json"),
        { MongoConfig( false, MongoServerInformation("127.0.0.1", 45678, "cloud", "simplecloud", "cloudpassword", "admin", "admin")) }
) {
}