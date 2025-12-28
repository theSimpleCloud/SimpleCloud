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

import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.OfflineCloudPlayer
import eu.thesimplecloud.jsonlib.JsonLib
import java.sql.*
import java.util.*


/**
 * Created by IntelliJ IDEA.
 * Date: 16.06.2020
 * Time: 14:18
 * @author Frederick Baier
 */
class SQLOfflineCloudPlayerHandler(private val databaseConnectionInformation: DatabaseConnectionInformation) :
    AbstractOfflineCloudPlayerHandler() {

    private val playerCollectionName = databaseConnectionInformation.collectionPrefix + "players"

    init {
        createDatabaseAndIndicesIfNotExist()
    }

    private fun createDatabaseAndIndicesIfNotExist() {
        if (!doesTableExist()) {
            getConnection().use { connection ->
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS `$playerCollectionName` (`uniqueId` varchar(36), `name` varchar(16), `data` LONGBLOB)")
                    .use { statement ->
                        statement.executeUpdate()
                        createIndex("uniqueId")
                        createIndex("name")
                    }
            }
        }
    }

    private fun getConnection(): Connection {
        return DriverManager.getConnection("jdbc:mysql://${databaseConnectionInformation.host}:${databaseConnectionInformation.port}/${databaseConnectionInformation.databaseName}?user=${databaseConnectionInformation.userName}&password=${databaseConnectionInformation.password}&serverTimezone=UTC&autoReconnect=true")
    }

    private fun createIndex(columnName: String) {
        getConnection().use { connection ->
            connection.prepareStatement("ALTER TABLE $playerCollectionName ADD INDEX ($columnName)")
                .use { statement ->
                    statement.executeUpdate()
                }
        }
    }

    override fun getOfflinePlayer(playerUniqueId: UUID): IOfflineCloudPlayer? {
        return loadPlayer(playerUniqueId.toString(), "uniqueId")
    }

    override fun getOfflinePlayer(name: String): IOfflineCloudPlayer? {
        return loadPlayer(name, "name")
    }

    private fun loadPlayer(value: String, fieldName: String): IOfflineCloudPlayer? = synchronized(this) {
        if (!exist(value, fieldName)) return null

        getConnection().use { conn ->
            conn.prepareStatement("SELECT `data` FROM `$playerCollectionName` WHERE `$fieldName` = ?")
                .use { statement ->
                    statement.setString(1, value)
                    val resultSet = statement.executeQuery()
                    val allDataStrings = getAllDataStringsFromResultSet(resultSet)
                    val players = allDataStrings.mapNotNull { loadPlayerFromJsonString(it) }
                    return getPlayerWithLatestLogin(players)
                }
        }
    }

    private fun getAllDataStringsFromResultSet(resultSet: ResultSet): List<String> {
        val returnList = mutableListOf<String>()
        while (resultSet.next()) {
            try {
                val dataString = resultSet.getString("data")
                returnList.add(dataString)
            } catch (e: SQLException) {
                //ignore exception
                //it will be thrown 2 times before reaching "data"
            }
        }
        return returnList
    }

    private fun loadPlayerFromJsonString(jsonString: String): OfflineCloudPlayer? {
        return JsonLib.fromJsonString(jsonString, databaseGson).getObject(OfflineCloudPlayer::class.java)
    }

    override fun saveCloudPlayer(offlineCloudPlayer: OfflineCloudPlayer): Unit = synchronized(this) {
        val newData = JsonLib.fromObject(offlineCloudPlayer, databaseGson).getAsJsonString()

        getConnection().use { connection ->
            if (!exist(offlineCloudPlayer.getUniqueId().toString(), "uniqueId")) {
                connection.prepareStatement("INSERT INTO `$playerCollectionName` (`uniqueId`, `name`, `data`) VALUES (?, ?, ?)")
                    .use { statement ->
                        statement.setString(1, offlineCloudPlayer.getUniqueId().toString())
                        statement.setString(2, offlineCloudPlayer.getName())
                        statement.setString(3, newData)
                        statement.executeUpdate()
                    }
            } else {
                connection.prepareStatement("UPDATE `$playerCollectionName` SET `data` = ?, `name` = ? WHERE `uniqueId` = ?")
                    .use { statement ->
                        statement.setString(1, newData)
                        statement.setString(2, offlineCloudPlayer.getName())
                        statement.setString(3, offlineCloudPlayer.getUniqueId().toString())
                        statement.executeUpdate()
                    }
            }
        }
    }

    override fun getRegisteredPlayerCount(): Int {
        getConnection().use { connection ->
            connection.prepareStatement("SELECT COUNT(*) FROM `$playerCollectionName`").use { statement ->
                val resultSet = statement.executeQuery()
                return if (!resultSet.next()) {
                    0
                } else {
                    resultSet.getInt(1)
                }
            }
        }
    }

    override fun closeConnection() {
        //nothing to do
    }

    private fun exist(searchValue: String, fieldName: String): Boolean {
        getConnection().use { connection ->
            connection.prepareStatement("SELECT `data` FROM `$playerCollectionName` WHERE `$fieldName` = ?")
                .use { statement ->
                    statement.setString(1, searchValue)
                    val resultSet = statement.executeQuery()
                    return resultSet.next()
                }
        }
    }

    private fun doesTableExist(): Boolean {
        getConnection().use { connection ->
            val meta: DatabaseMetaData = connection.metaData
            val res = meta.getTables(null, null, this.playerCollectionName, arrayOf("TABLE"))
            return res.next()
        }
    }

}