package eu.thesimplecloud.module.statistics.rest.overview.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.module.statistics.StatisticsModule
import eu.thesimplecloud.module.statistics.timed.TimedValue
import eu.thesimplecloud.module.statistics.timed.store.ITimedValueStore
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max


data class Overview(
    val personality: ServerPersonality,
    val installDate: Long,
    val startedServers: Int,
    val startedServerRecord: TimedValue<Int>,
    val players: Int,
    val playerAverage: Int,
    val playerRecord: TimedValue<Int>,
    val topPlayers: SortedMap<UUID, Int>,
    val topFavoriteServer: Array<LabyServer?>,
    val joins: Int,
    val weekAverage: Array<DayAverage>,
) {

    companion object {

        private const val USER_STATS_FORMAT = "https://laby.net/api/v3/user/%s/game-stats"
        fun create(force: Boolean) : Overview {

            if (!force)
            {
                val saved = get()
                if(saved != null) return saved
            }

            val playerJoins: ITimedValueStore<*> = (StatisticsModule.instance.getValueStoreByName("cloud_stats_player_connects"))!!
            val joins = playerJoins.count()

            val serverStarts: ITimedValueStore<*> = (StatisticsModule.instance.getValueStoreByName("cloud_stats_service_starts"))!!
            val startedServers = serverStarts.count()

            var playerRecord = TimedValue(-1)
            var serversRecord = TimedValue(-1)

            val currentYear: Long = GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), 0, 0).timeInMillis
            val currentTime = System.currentTimeMillis()

            val calender = Calendar.getInstance()
            var installDate = serverStarts.get(0, currentTime)[0].getTimeStamp()
            calender.time = Date(installDate)
            installDate = calender.get(Calendar.DAY_OF_YEAR) * 24L * 60L * 60 * 1000L + currentYear
            val dates = getDates(currentYear.coerceAtLeast(installDate), currentTime)


            val playerUniqueJoins = hashMapOf<UUID, Int>()
            val weekAverage = arrayOf(DayAverage.empty(), DayAverage.empty(), DayAverage.empty(), DayAverage.empty(), DayAverage.empty(), DayAverage.empty(), DayAverage.empty())
            var averageUniqueJoins = 0
            dates.forEach { date ->
                // Generate player record, server record, ... for every day in the past year
                val nextDate = date.time + 24L * 60L * 60L * 1000L

                val uniqueJoins = mutableListOf<UUID>()
                val allJoins = playerJoins.get(date.time, nextDate)
                val starts = serverStarts.get(date.time, nextDate).count()

                // Get the average for the current week day
                val calendar = Calendar.getInstance()
                calendar.time = date
                val currentWeekDay = calendar.get(Calendar.DAY_OF_WEEK)
                val currentAverage = weekAverage[currentWeekDay - 1]

                //Handle joins for each day
                allJoins.forEach { join ->
                    if (!uniqueJoins.contains(join.value))
                        uniqueJoins.add(UUID.fromString(join.value.toString()))
                    playerUniqueJoins[UUID.fromString(join.value.toString())] = playerUniqueJoins.getOrDefault(join.value, 0) + 1
                }

                //Calculate new player and server records
                if (playerRecord.value < uniqueJoins.size) {
                    playerRecord = TimedValue(uniqueJoins.size, date.time)
                }
                if (serversRecord.value < starts) {
                    serversRecord = TimedValue(starts, date.time)
                }

                currentAverage.addAverage(starts, allJoins.size, uniqueJoins.size)
                averageUniqueJoins += uniqueJoins.size
            }


            //Sort players according to most joins
            val sortedUniquePlayers = playerUniqueJoins.toSortedMap (
                compareBy<UUID> { -playerUniqueJoins.getOrDefault(it, 0) }.thenBy { it }
            )

            //Cleanup player calculations
            val playerUniqueJoinsArray: Array<out UUID> = playerUniqueJoins.keys.toTypedArray()
            val uniquePlayers = playerUniqueJoinsArray.size

            val playerAverage = averageUniqueJoins / if (dates.isNotEmpty()) dates.size else 1
            val topPlayers = hashMapOf<UUID, Int>()
            val sortedPlayersIterator = sortedUniquePlayers.keys.iterator()
            for(maxTopPlayers in 0..4) {
                if(!sortedPlayersIterator.hasNext()) {
                    continue
                }
                if(sortedUniquePlayers.size > maxTopPlayers) {
                    val key = sortedPlayersIterator.next()
                    topPlayers[key] = playerUniqueJoins.getOrDefault(key, -1)
                }
            }
            val topPlayersSorted = topPlayers.toSortedMap(
                compareBy<UUID> { -topPlayers.getOrDefault(it, 0) }.thenBy { it }
            )


            //Retrieve the players top servers according to laby.net
            val topServersHashMap = hashMapOf<LabyServer, Int>()
            val client = OkHttpClient()
            for (player in playerUniqueJoinsArray) {
                val server = requestTopServer(player, client) ?: continue
                topServersHashMap[server] = topServersHashMap.getOrDefault(server, 0) + 1
            }

            //Cleanup top server calculations
            val topServersArray: Array<out LabyServer> = topServersHashMap.keys.toTypedArray()
            val topServer = arrayOf(if (topServersArray.isNotEmpty()) topServersArray[0] else null)

            for(average in weekAverage) {
                average.calculateAverage()
            }

            //TODO: Add ServerPersonality detection
            val generatedOverview = Overview(ServerPersonality.NEWCOMER, installDate, startedServers, playerRecord, uniquePlayers, playerAverage, playerRecord, topPlayersSorted, topServer, joins, weekAverage)
            save(generatedOverview)
            return generatedOverview
        }

        private fun save(overview: Overview)
        {
            val file = File("modules/statistics/overview_latest.json")
            if(!file.exists()) {
                if(!file.parentFile.exists()) file.parentFile.mkdirs()
                file.createNewFile()
            }
            val gson = GsonBuilder().setPrettyPrinting().create()
            val writer = FileWriter(file)
            gson.toJson(overview, writer)
            writer.close()
        }

        private fun get() : Overview? {
            try{
                val file = File("modules/statistics/overview_latest.json")
                if (!file.exists()) return null
                val gson = Gson()
                return gson.fromJson(FileReader(file), Overview::class.java)
            }catch (e: Exception)
            {
                return null
            }
        }


        private fun requestTopServer(uuid: UUID, client: OkHttpClient) : LabyServer? {
            val request = Request.Builder()
                .url(USER_STATS_FORMAT.format(uuid.toString()))
                .get()
                .build()
            val response = client.newCall(request).execute()
            if(response.body == null || response.code != 200) return null
            val body = response.body!!.string()
            val bodyJson = JsonParser.parseString(body).asJsonObject
            response.close()
            if(!bodyJson.has("most_played_server")) return null
            val serverObject = bodyJson.getAsJsonObject("most_played_server")
            if(!serverObject.has("nice_name") || !serverObject.has("direct_ip")) return null
            val serverIp = serverObject.get("direct_ip").asString
            val niceName = serverObject.get("nice_name").asString
            if(serverIp.equals("unknown") || niceName.equals("unknown")) return null

            return LabyServer(niceName, serverIp)
        }

        private fun getDates(date1Millis: Long, date2Millis: Long): List<Date> {
            val dates = ArrayList<Date>()
            val date1 = Date(date1Millis)
            val date2 = Date(date2Millis)
            val cal1 = Calendar.getInstance()
            cal1.setTime(date1)
            val cal2 = Calendar.getInstance()
            cal2.setTime(date2)
            while (!cal1.after(cal2)) {
                dates.add(cal1.time)
                cal1.add(Calendar.DATE, 1)
            }
            return dates
        }
    }
}