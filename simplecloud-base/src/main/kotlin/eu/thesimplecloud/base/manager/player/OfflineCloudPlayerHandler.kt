/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.base.manager.player

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Indexes
import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.OfflineCloudPlayer
import eu.thesimplecloud.api.player.connection.DefaultPlayerAddress
import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.base.manager.mongo.MongoConnectionInformation
import eu.thesimplecloud.base.manager.player.exception.OfflinePlayerLoadException
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.jsonlib.JsonLib
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
            loadCollection.createIndex(Indexes.text("name"))
            //loadCollection.createIndex(Indexes.text("uniqueId"))

            //dummy request
            val playerUniqueId = UUID.randomUUID()
            val dummyPlayer = getOfflinePlayer(playerUniqueId)
            if (dummyPlayer == null) {
                val playerConnection = DefaultPlayerConnection(DefaultPlayerAddress("127.0.0.1", 0), "Test", playerUniqueId, true, 42)
                saveCloudPlayer(OfflineCloudPlayer("Test", playerUniqueId, 1L, 1L, 1L, playerConnection))
                delay(100)
                deletePlayer(playerUniqueId)
            }
        }
    }

    private fun deletePlayer(playerUniqueId: UUID) {
        this.loadCollection.deleteOne(Filters.eq("uniqueId", playerUniqueId))
    }

    override fun getOfflinePlayer(playerUniqueId: UUID): IOfflineCloudPlayer? {
        return fromLoadOfflinePlayer(this.loadCollection.findOne(Filters.eq("uniqueId", playerUniqueId)))
    }

    override fun getOfflinePlayer(name: String): IOfflineCloudPlayer? {
        return fromLoadOfflinePlayer(this.loadCollection.findOne("{ \$text: { \$search: \"$name\",\$caseSensitive :false } }"))
    }

    @Synchronized
    override fun saveCloudPlayer(offlineCloudPlayer: OfflineCloudPlayer) {
        //load all properties so that the values are all set
        offlineCloudPlayer.getProperties().forEach { it.value.getValue() }
        if (offlineCloudPlayer::class.java != OfflineCloudPlayer::class.java) throw IllegalStateException("Cannot save player of type " + offlineCloudPlayer::class.java.simpleName)
        if (getOfflinePlayer(offlineCloudPlayer.getUniqueId()) != null) {
            this.saveCollection.replaceOne(Filters.eq("uniqueId", offlineCloudPlayer.getUniqueId()), offlineCloudPlayer)
        } else {
            this.saveCollection.insertOne(offlineCloudPlayer)
        }

    }

    private fun fromLoadOfflinePlayer(loadOfflineCloudPlayer: LoadOfflineCloudPlayer?): OfflineCloudPlayer? {
        loadOfflineCloudPlayer ?: return null
        val propertyMapAsDocument = loadOfflineCloudPlayer.propertyMap
        try {
            val propertyMap = propertyMapAsDocument.toSortedMap().mapValues {
                val valueString = it.value.toString()
                val jsonLib = JsonLib.fromJsonString(valueString)
                val className = jsonLib.getString("className")!!
                val clazz = this.findClass(className)
                val value = jsonLib.getObject("savedValue", clazz)!!
                Property(value)
            }
            return OfflineCloudPlayer(loadOfflineCloudPlayer.name, loadOfflineCloudPlayer.uniqueId, loadOfflineCloudPlayer.firstLogin, loadOfflineCloudPlayer.lastLogin, loadOfflineCloudPlayer.onlineTime, loadOfflineCloudPlayer.lastPlayerConnection, HashMap(propertyMap))
        } catch (ex: Exception) {
            throw OfflinePlayerLoadException("Error while loading OfflinePlayer ${loadOfflineCloudPlayer.name}:", ex)
        }
    }

    private fun findClass(className: String): Class<*> {
        return Manager.instance.cloudModuleHandler.findModuleOrSystemClass(className)
    }


}