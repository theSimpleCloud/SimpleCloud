package eu.thesimplecloud.base.manager.config

import eu.thesimplecloud.api.config.AbstractJsonDataConfigLoader
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.base.manager.mongo.MongoConnectionInformation
import java.io.File

class MongoConfigLoader : AbstractJsonDataConfigLoader<MongoConnectionInformation>(
        MongoConnectionInformation::class.java,
        File(DirectoryPaths.paths.storagePath + "mongo.json"),
        { MongoConnectionInformation("127.0.0.1", 45678, "cloud", "simplecloud", "cloudpassword", "") }
) {
}