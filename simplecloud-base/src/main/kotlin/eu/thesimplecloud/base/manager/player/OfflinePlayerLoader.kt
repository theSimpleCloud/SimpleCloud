package eu.thesimplecloud.base.manager.player

import eu.thesimplecloud.base.manager.mongo.MongoConnectionInformation
import java.util.*
import org.litote.kmongo.*

class OfflinePlayerLoader(mongoServerConfig: MongoConnectionInformation) : IOfflinePlayerLoader {

    val mongoClient = KMongo.createClient(mongoServerConfig.host, mongoServerConfig.port)


    override fun getOfflinePlayer(playerUniqueId: UUID) {

    }


}