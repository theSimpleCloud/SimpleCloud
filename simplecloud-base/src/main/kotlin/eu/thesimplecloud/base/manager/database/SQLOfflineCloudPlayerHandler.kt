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

package eu.thesimplecloud.base.manager.database

import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.OfflineCloudPlayer
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.launcher.startup.Launcher
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by IntelliJ IDEA.
 * Date: 16.06.2020
 * Time: 14:18
 * @author Frederick Baier
 */
class SQLOfflineCloudPlayerHandler(private val databaseConnectionInformation: DatabaseConnectionInformation) : AbstractOfflineCloudPlayerHandler() {

    private var connection: Connection? = null

    private val playerCollectionName = databaseConnectionInformation.collectionPrefix + "players"

    init {
        runReconnectLoop()
        createDatabaseAndIndicesIfNotExist()
    }

    private fun createDatabaseAndIndicesIfNotExist() {
        val dbm: DatabaseMetaData = connection!!.metaData
        val tables = dbm.getTables(null, null, this.playerCollectionName, null)
        if (!tables.next()) {  // Table does not exist
            val statement = connection!!.prepareStatement("CREATE TABLE IF NOT EXISTS `$playerCollectionName` (`uniqueId` varchar(36), `name` varchar(16), `data` LONGBLOB)")
            statement.executeUpdate()
            createIndex("uniqueId")
            createIndex("name")
        }
    }

    private fun runReconnectLoop() {
        reconnect()
        Launcher.instance.scheduler.scheduleAtFixedRate({
            reconnect()
        }, 1, 1, TimeUnit.HOURS)
    }

    private fun reconnect() {
        closeConnection()
        this.connection = DriverManager.getConnection("jdbc:mysql://${databaseConnectionInformation.host}:${databaseConnectionInformation.port}/${databaseConnectionInformation.databaseName}?user=${databaseConnectionInformation.userName}&password=${databaseConnectionInformation.password}&serverTimezone=UTC&autoReconnect=true")
    }

    private fun createIndex(columnName: String) {
        val statement = connection!!.prepareStatement("ALTER TABLE $playerCollectionName ADD INDEX ($columnName)")
        statement.executeUpdate()
    }

    override fun getOfflinePlayer(playerUniqueId: UUID): IOfflineCloudPlayer? {
        return loadPlayer(playerUniqueId.toString(), "uniqueId")
    }

    override fun getOfflinePlayer(name: String): IOfflineCloudPlayer? {
        return loadPlayer(name, "name")
    }

    private fun loadPlayer(value: String, fieldName: String): OfflineCloudPlayer? {
        if (!exist(value, fieldName)) return null
        val statement = connection!!.prepareStatement("SELECT `data` FROM `$playerCollectionName` WHERE `$fieldName` = ?")
        statement.setString(1, value)
        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            try {
                val dataString = resultSet.getString("data")
                return loadPlayerFromJsonString(dataString)
            } catch (e: SQLException) {
                //ignore exception
                //it will be thrown 2 times before reaching "data"
            }
        }
        throw SQLException()
    }

    private fun loadPlayerFromJsonString(jsonString: String): OfflineCloudPlayer? {
        return JsonLib.fromJsonString(jsonString, databaseGson).getObject(OfflineCloudPlayer::class.java)
    }

    override fun saveCloudPlayer(offlineCloudPlayer: OfflineCloudPlayer) {
        val newData = JsonLib.fromObject(offlineCloudPlayer, databaseGson).getAsJsonString()
        if (!exist(offlineCloudPlayer.getUniqueId().toString(), "uniqueId")) {
            val statement = connection!!.prepareStatement("INSERT INTO `$playerCollectionName` (`uniqueId`, `name`, `data`) VALUES (?, ?, ?)")
            statement.setString(1, offlineCloudPlayer.getUniqueId().toString())
            statement.setString(2, offlineCloudPlayer.getName())
            statement.setString(3, newData)
            statement.executeUpdate()
        } else {
            val statement = connection!!.prepareStatement("UPDATE `$playerCollectionName` SET `data` = ?, `name` = ? WHERE `uniqueId` = ?")
            statement.setString(1, newData)
            statement.setString(2, offlineCloudPlayer.getName())
            statement.setString(3, offlineCloudPlayer.getUniqueId().toString())
            statement.executeUpdate()
        }
        //update name
    }

    override fun closeConnection() {
        connection?.close()
    }

    private fun exist(searchValue: String, fieldName: String): Boolean {
        val prepareStatement = connection!!.prepareStatement("SELECT `data` FROM `$playerCollectionName` WHERE `$fieldName` = ?")
        prepareStatement.setString(1, searchValue)
        val resultSet = prepareStatement.executeQuery()
        return resultSet.next()
    }


}