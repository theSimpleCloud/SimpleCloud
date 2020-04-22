package eu.thesimplecloud.base.manager.player

import com.mongodb.client.model.Filters
import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.OfflineCloudPlayer
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.base.manager.mongo.MongoConnectionInformation
import eu.thesimplecloud.base.manager.player.exception.OfflinePlayerLoadException
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.litote.kmongo.createIndex
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import java.util.*
import kotlin.collections.HashMap

class OfflineCloudPlayerHandler(mongoConnectionInformation: MongoConnectionInformation) : IOfflineCloudPlayerHandler {

    private val loadCollection = Manager.instance.mongoClient.getDatabase(mongoConnectionInformation.databaseName).getCollection<LoadOfflineCloudPlayer>(mongoConnectionInformation.collectionPrefix + "players")
    private val saveCollection = Manager.instance.mongoClient.getDatabase(mongoConnectionInformation.databaseName).getCollection<OfflineCloudPlayer>(mongoConnectionInformation.collectionPrefix + "players")

    init {
        //make a first request (the first request will take a very long time when using embed mongodb. Following requests will be way faster)
        GlobalScope.launch {
            loadCollection.findOne()
            loadCollection.createIndex("{ name: \"text\" }")
        }
    }

    override fun getOfflinePlayer(playerUniqueId: UUID): IOfflineCloudPlayer? {
        return fromLoadOfflinePlayer(this.loadCollection.findOne(Filters.eq("uniqueId", playerUniqueId)))
    }

    override fun getOfflinePlayer(name: String): IOfflineCloudPlayer? {
        return fromLoadOfflinePlayer(this.loadCollection.findOne("{ \$text: { \$search: \"$name\",\$caseSensitive :false } }"))
    }

    @Synchronized
    override fun saveCloudPlayer(offlineCloudPlayer: OfflineCloudPlayer) {
        if (offlineCloudPlayer::class.java != OfflineCloudPlayer::class.java) throw IllegalStateException("Cannot save player of type " + offlineCloudPlayer::class.java.simpleName)
        if (getOfflinePlayer(offlineCloudPlayer.getUniqueId()) != null)
            this.saveCollection.replaceOne(Filters.eq("uniqueId", offlineCloudPlayer.getUniqueId()), offlineCloudPlayer)
        else
            this.saveCollection.insertOne(offlineCloudPlayer)

    }

    private fun fromLoadOfflinePlayer(loadOfflineCloudPlayer: LoadOfflineCloudPlayer?): OfflineCloudPlayer? {
        loadOfflineCloudPlayer ?: return null
        val propertyMapAsDocument = loadOfflineCloudPlayer.propertyMap
        try {
            val propertyMap = propertyMapAsDocument.toSortedMap().mapValues {
                val valueString = it.value.toString()
                val jsonData = JsonData.fromJsonString(valueString)
                val className = jsonData.getString("className")!!
                val clazz = Manager.instance.cloudModuleHandler.findModuleClass(className)
                val value = jsonData.getObject("savedValue", clazz)!!
                Property(value)
            }
            return OfflineCloudPlayer(loadOfflineCloudPlayer.name, loadOfflineCloudPlayer.uniqueId, loadOfflineCloudPlayer.firstLogin, loadOfflineCloudPlayer.lastLogin, loadOfflineCloudPlayer.onlineTime, loadOfflineCloudPlayer.lastPlayerConnection, HashMap(propertyMap))
        } catch (ex: Exception) {
            throw OfflinePlayerLoadException("Error while loading OfflinePlayer ${loadOfflineCloudPlayer.name}:", ex)
        }
    }


}