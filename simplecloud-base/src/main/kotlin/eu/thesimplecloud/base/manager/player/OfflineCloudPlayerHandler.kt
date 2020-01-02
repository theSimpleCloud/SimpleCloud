package eu.thesimplecloud.base.manager.player

import com.mongodb.client.model.Filters
import eu.thesimplecloud.base.manager.mongo.MongoConnectionInformation
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.lib.player.IOfflineCloudPlayer
import eu.thesimplecloud.lib.player.OfflineCloudPlayer
import java.util.*
import org.litote.kmongo.*

class OfflineCloudPlayerHandler(mongoConnectionInformation: MongoConnectionInformation) : IOfflineCloudPlayerHandler {

    private val playersCollection = Manager.instance.mongoClient.getDatabase(mongoConnectionInformation.databaseName).getCollection<OfflineCloudPlayer>("players")

    override fun getOfflinePlayer(playerUniqueId: UUID): IOfflineCloudPlayer? {
        return this.playersCollection.findOne(Filters.eq("uniqueId", playerUniqueId))
    }

    override fun getOfflinePlayer(name: String): IOfflineCloudPlayer? {
        return this.playersCollection.findOne(Filters.eq("name", name))
    }

    override fun saveCloudPlayer(offlineCloudPlayer: OfflineCloudPlayer) {
        if (getOfflinePlayer(offlineCloudPlayer.getUniqueId()) != null)
            this.playersCollection.replaceOne(Filters.eq("uniqueId", offlineCloudPlayer.getUniqueId()), offlineCloudPlayer)
        else
            this.playersCollection.insertOne(offlineCloudPlayer)

    }


}