package eu.thesimplecloud.base.manager.player

import com.mongodb.client.model.Filters
import eu.thesimplecloud.base.manager.mongo.MongoConnectionInformation
import eu.thesimplecloud.lib.player.IOfflineCloudPlayer
import eu.thesimplecloud.lib.player.OfflineCloudPlayer
import jodd.util.SystemUtil
import org.elasticsearch.common.lang3.SystemUtils
import java.util.*
import org.litote.kmongo.*

class OfflinePlayerLoader(mongoConnectionInformation: MongoConnectionInformation) : IOfflinePlayerLoader {

    val mongoClient = KMongo.createClient(mongoConnectionInformation.getConnectionString())

    val playersCollection = mongoClient.getDatabase("cloud").getCollection<OfflineCloudPlayer>("players")

    override fun getOfflinePlayer(playerUniqueId: UUID): IOfflineCloudPlayer? {
        return playersCollection.findOne(Filters.eq("uniqueId", playerUniqueId))
    }


}