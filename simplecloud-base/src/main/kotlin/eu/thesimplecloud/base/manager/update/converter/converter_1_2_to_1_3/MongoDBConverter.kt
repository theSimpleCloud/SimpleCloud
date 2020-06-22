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

package eu.thesimplecloud.base.manager.update.converter.converter_1_2_to_1_3

import com.mongodb.ConnectionString
import com.mongodb.MongoClient
import eu.thesimplecloud.api.player.OfflineCloudPlayer
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.base.manager.player.LoadOfflineCloudPlayer
import eu.thesimplecloud.base.manager.player.exception.OfflinePlayerLoadException
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.jsonlib.JsonLib
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import java.io.File


/**
 * Created by IntelliJ IDEA.
 * Date: 18.06.2020
 * Time: 12:51
 * @author Frederick Baier
 */
class MongoDBConverter {

    fun convert() {
        val config = JsonLib.fromJsonFile(File("storage/mongo.json")) ?: return
        val host = config.getString("host")!!
        val port = config.getInt("port")!!
        val databaseName = config.getString("databaseName")!!
        val userName = config.getString("userName")!!
        val password = config.getString("password")!!
        val collectionPrefix = config.getString("collectionPrefix")!!
        val mongoClient = createMongoClient(host, port, databaseName, password, userName)

        val collection = mongoClient.getDatabase(databaseName)
                .getCollection<LoadOfflineCloudPlayer>(collectionPrefix + "players")

        collection.drop()

        File("storage/mongo.json").delete()
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

    private fun getConnectionString(host: String, port: Int, databaseName: String, password: String, userName: String): ConnectionString {
        if (password.isBlank() || userName.isBlank()) {
            return ConnectionString("mongodb://$host:$port/$databaseName")
        }

        return ConnectionString("mongodb://$userName:$password@$host:$port/$databaseName")
    }

    private fun createMongoClient(host: String, port: Int, databaseName: String, password: String, userName: String): MongoClient {
        return KMongo.createClient(getConnectionString(host, port, databaseName, password, userName))
    }

    private fun findClass(className: String): Class<*> {
        val clazz = kotlin.runCatching {
            Manager.instance.cloudModuleHandler.findModuleClass(className)
        }.getOrNull()
        if (clazz != null) return clazz

        val classLoader = Manager.instance.appClassLoader
        return Class.forName(className, true, classLoader)

    }

}