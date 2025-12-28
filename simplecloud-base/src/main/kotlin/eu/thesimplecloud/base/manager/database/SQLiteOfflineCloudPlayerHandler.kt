package eu.thesimplecloud.base.manager.database

import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.OfflineCloudPlayer
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File
import java.sql.*
import java.util.*
import java.util.concurrent.TimeUnit

class SQLiteOfflineCloudPlayerHandler(private val databaseConnectionInformation: DatabaseConnectionInformation) :
    AbstractOfflineCloudPlayerHandler() {

    private val databaseFile = File("storage/database.db")

    private val playerCollectionName = databaseConnectionInformation.collectionPrefix + "players"

    init {
        if (!databaseFile.exists()) {
            databaseFile.createNewFile()
        }

        //Class.forName("org.sqlite.JDBC");

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
        return DriverManager.getConnection("jdbc:sqlite:${databaseFile.path}")
    }

    private fun createIndex(columnName: String) {
        getConnection().use { connection ->
            connection.prepareStatement("CREATE INDEX ${columnName} ON $playerCollectionName ($columnName)")
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