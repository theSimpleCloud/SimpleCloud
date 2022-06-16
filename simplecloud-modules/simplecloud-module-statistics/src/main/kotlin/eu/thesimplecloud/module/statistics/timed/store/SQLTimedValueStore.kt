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

package eu.thesimplecloud.module.statistics.timed.store

import eu.thesimplecloud.base.manager.database.SQLOfflineCloudPlayerHandler
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.module.statistics.timed.TimedValue
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.ResultSet

/**
 * Created by IntelliJ IDEA.
 * Date: 25.12.2020
 * Time: 12:43
 * @author Frederick Baier
 */
class SQLTimedValueStore<T : Any>(
    private val classOfT: Class<T>,
    private val collectionName: String
) : ITimedValueStore<T> {

    init {
        createDatabaseAndIndicesIfNotExist()
    }

    private fun getConnection(): Connection {
        val playerHandler = Manager.instance.offlineCloudPlayerHandler as SQLOfflineCloudPlayerHandler
        return playerHandler.connection!!
    }

    private fun createDatabaseAndIndicesIfNotExist() {
        if (!doesTableExist()) {
            val statement =
                getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `$collectionName` (`value` varchar(36), `timestamp` BIGINT(20))")
            statement.executeUpdate()
            createIndex("timestamp")
        }
    }

    override fun store(timedValue: TimedValue<T>) {
        val statement =
            getConnection().prepareStatement("INSERT INTO `$collectionName` (`value`, `timestamp`) VALUES (?, ?)")
        statement.setString(1, timedValue.value.toString())
        statement.setLong(2, timedValue.getTimeStamp())
        statement.executeUpdate()
    }


    private fun createIndex(columnName: String) {
        val statement = getConnection().prepareStatement("ALTER TABLE $collectionName ADD INDEX ($columnName)")
        statement.executeUpdate()
    }

    private fun doesTableExist(): Boolean {
        val meta: DatabaseMetaData = getConnection().metaData
        val res = meta.getTables(null, null, this.collectionName, arrayOf("TABLE"))
        return res.next()
    }

    override fun getAll(): List<TimedValue<T>> {
        val statement = getConnection().prepareStatement("SELECT * FROM `$collectionName`")
        val resultSet = statement.executeQuery()
        return getAllTimedValuesFromResultSet(resultSet)
    }

    override fun get(fromTimeStamp: Long, toTimeStamp: Long): List<TimedValue<T>> {
        val statement =
            getConnection().prepareStatement("SELECT * FROM `$collectionName` WHERE timestamp BETWEEN $fromTimeStamp and $toTimeStamp ORDER BY timestamp ASC")
        val resultSet = statement.executeQuery()
        return getAllTimedValuesFromResultSet(resultSet)
    }

    private fun getAllTimedValuesFromResultSet(resultSet: ResultSet): List<TimedValue<T>> {
        val returnList = ArrayList<TimedValue<T>>()
        while (resultSet.next()) {
            val value = resultSet.getString(1)
            val timeStamp = resultSet.getLong(2)
            returnList.add(TimedValue(JsonLib.fromJsonString(value).getObject(classOfT), timeStamp))
        }
        return returnList
    }

    override fun getCollectionName(): String {
        return this.collectionName
    }

    override fun count(): Int {
        val statement = getConnection().prepareStatement("SELECT COUNT(*) FROM `$collectionName`")
        val resultSet = statement.executeQuery()
        return if (!resultSet.next()) {
            0
        } else {
            resultSet.getInt(1)
        }
    }

}