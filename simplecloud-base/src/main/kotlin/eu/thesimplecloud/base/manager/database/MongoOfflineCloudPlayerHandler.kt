/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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

package eu.thesimplecloud.base.manager.database

import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Indexes
import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.OfflineCloudPlayer
import eu.thesimplecloud.api.player.connection.DefaultPlayerAddress
import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection
import eu.thesimplecloud.jsonlib.JsonLib
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bson.Document
import org.litote.kmongo.KMongo
import org.litote.kmongo.find
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import java.util.*

class MongoOfflineCloudPlayerHandler(val databaseConnectionInformation: DatabaseConnectionInformation) :
    AbstractOfflineCloudPlayerHandler() {

    private val mongoClient: MongoClient = createMongoClient()
    val database: MongoDatabase = this.mongoClient.getDatabase(databaseConnectionInformation.databaseName)
    private val collection =
        database.getCollection<Document>(databaseConnectionInformation.collectionPrefix + "players")

    init {
        //make a first request (the first request will take a very long time when using embed mongodb. Following requests will be way faster)
        GlobalScope.launch {
            collection.createIndex(Indexes.text("name"))
            //loadCollection.createIndex(Indexes.text("uniqueId"))

            //dummy request
            val playerUniqueId = UUID.randomUUID()
            val dummyPlayer = getOfflinePlayer(playerUniqueId)
            if (dummyPlayer == null) {
                val playerConnection =
                    DefaultPlayerConnection(DefaultPlayerAddress("127.0.0.1", 0), "Test", playerUniqueId, true, 42)
                saveCloudPlayer(OfflineCloudPlayer("Test", playerUniqueId, 1L, 1L, 1L, playerConnection))
                delay(100)
                deletePlayer(playerUniqueId)
            }
        }
    }

    private fun getConnectionString(): ConnectionString {
        val host = databaseConnectionInformation.host
        val port = databaseConnectionInformation.port
        val databaseName = databaseConnectionInformation.databaseName
        val userName = databaseConnectionInformation.userName
        val password = databaseConnectionInformation.password
        if (password.isBlank() || userName.isBlank()) {
            return ConnectionString("mongodb://$host:$port/$databaseName")
        }

        return ConnectionString("mongodb://$userName:$password@$host:$port/$databaseName")
    }

    private fun createMongoClient(): MongoClient {
        return KMongo.createClient(getConnectionString())
    }

    private fun deletePlayer(playerUniqueId: UUID) {
        this.collection.deleteOne(Filters.eq("uniqueId", playerUniqueId.toString()))
    }

    override fun getOfflinePlayer(playerUniqueId: UUID): IOfflineCloudPlayer? {
        val document = this.collection.findOne(Filters.eq("uniqueId", playerUniqueId.toString()))
            ?: return null
        return JsonLib.fromObject(document, databaseGson).getObject(OfflineCloudPlayer::class.java)
    }

    override fun getOfflinePlayer(name: String): IOfflineCloudPlayer? {
        val documents = this.collection.find("{ \$text: { \$search: \"$name\",\$caseSensitive :false } }")
        val players = documents.map {
            JsonLib.fromObject(it, databaseGson).getObject(OfflineCloudPlayer::class.java)
        }
        return getPlayerWithLatestLogin(players.toList())
    }

    @Synchronized
    override fun saveCloudPlayer(offlineCloudPlayer: OfflineCloudPlayer) {
        //load all properties so that the values are all set
        val documentToSave = JsonLib.fromObject(offlineCloudPlayer, databaseGson).getObject(Document::class.java)
        if (offlineCloudPlayer::class.java != OfflineCloudPlayer::class.java) throw IllegalStateException("Cannot save player of type " + offlineCloudPlayer::class.java.simpleName)
        if (getOfflinePlayer(offlineCloudPlayer.getUniqueId()) != null) {
            this.collection.replaceOne(
                Filters.eq("uniqueId", offlineCloudPlayer.getUniqueId().toString()),
                documentToSave
            )
        } else {
            this.collection.insertOne(documentToSave)
        }

    }

    override fun getRegisteredPlayerCount(): Int {
        return this.collection.countDocuments().toInt()
    }

    override fun closeConnection() {
        this.mongoClient.close()
    }


}