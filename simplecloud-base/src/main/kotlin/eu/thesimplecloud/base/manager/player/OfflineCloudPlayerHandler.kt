package eu.thesimplecloud.base.manager.player

import com.mongodb.client.model.Filters
import eu.thesimplecloud.base.manager.mongo.MongoConnectionInformation
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.OfflineCloudPlayer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import org.litote.kmongo.*

class OfflineCloudPlayerHandler(mongoConnectionInformation: MongoConnectionInformation) : IOfflineCloudPlayerHandler {

    private val playersCollection = Manager.instance.mongoClient.getDatabase(mongoConnectionInformation.databaseName).getCollection<OfflineCloudPlayer>(mongoConnectionInformation.collectionPrefix + "players")

    init {
        //make a first request (the first request will take a very long time when using embed mongodb. Following requests will be way faster)
        GlobalScope.launch {
            val player = playersCollection.findOne()
            if (player != null) {
                getOfflinePlayer(player.getUniqueId())
            }
            playersCollection.createIndex("{ name: \"text\" }")
        }
    }

    override fun getOfflinePlayer(playerUniqueId: UUID): IOfflineCloudPlayer? {
        return this.playersCollection.findOne(Filters.eq("uniqueId", playerUniqueId))
    }

    override fun getOfflinePlayer(name: String): IOfflineCloudPlayer? {
        return this.playersCollection.findOne("{ \$text: { \$search: \"$name\",\$caseSensitive :false } }")
    }

    override fun saveCloudPlayer(offlineCloudPlayer: OfflineCloudPlayer) {
        if (getOfflinePlayer(offlineCloudPlayer.getUniqueId()) != null)
            this.playersCollection.replaceOne(Filters.eq("uniqueId", offlineCloudPlayer.getUniqueId()), offlineCloudPlayer)
        else
            this.playersCollection.insertOne(offlineCloudPlayer)

    }


}