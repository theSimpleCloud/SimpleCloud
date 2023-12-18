package eu.thesimplecloud.module.statistics.rest.overview.data

import com.google.gson.JsonParser
import eu.thesimplecloud.module.statistics.StatisticsModule
import eu.thesimplecloud.module.statistics.timed.TimedValue
import eu.thesimplecloud.module.statistics.timed.store.ITimedValueStore
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*
import kotlin.collections.HashMap


data class Overview(
    val personality: ServerPersonality,
    val installDate: Long,
    val startedServers: Int,
    val startedServerRecord: TimedValue<Int>,
    val players: Int,
    val playerAverage: Int,
    val playerRecord: TimedValue<Int>,
    val topPlayers: Array<UUID?>,
    val topFavoriteServers: Array<LabyServer?>,
    val joins: Int,
    val weekAverage: Array<DayAverage>,
) {

    companion object {

        private const val USER_STATS_FORMAT = "https://laby.net/api/v3/user/%s/game-stats"
        fun create() : Overview {
            val playerJoins: ITimedValueStore<UUID> = (StatisticsModule.instance.getValueStoreByName("cloud_stats_player_connects") as ITimedValueStore<UUID>?)!!
            val joins = playerJoins.count()

            val serverStarts: ITimedValueStore<String> = (StatisticsModule.instance.getValueStoreByName("cloud_stats_service_starts") as ITimedValueStore<String>?)!!
            val startedServers = serverStarts.count()

            var playerRecord = TimedValue(-1)
            var serversRecord = TimedValue(-1)

            val currentYear: Long = (Calendar.getInstance().get(Calendar.YEAR) - 1900) * 365 * 24 * 60L * 60L * 1000L
            val currentTime = System.currentTimeMillis()

            val dates = getDates(currentYear, currentTime)
            val installDate = serverStarts.get(0, currentTime)[0].getTimeStamp()

            val playerUniqueJoins = hashMapOf<UUID, Int>()
            val weekAverage = arrayOf(DayAverage.empty(), DayAverage.empty(), DayAverage.empty(), DayAverage.empty(), DayAverage.empty(), DayAverage.empty(), DayAverage.empty())

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
                        uniqueJoins.add(join.value)
                    playerUniqueJoins[join.value] = playerUniqueJoins.getOrDefault(join.value, 0) + 1
                }

                //Calculate new player and server records
                if (playerRecord.value < uniqueJoins.size) {
                    playerRecord = TimedValue(uniqueJoins.size, date.time)
                }
                if (serversRecord.value < starts) {
                    serversRecord = TimedValue(starts, date.time)
                }

                currentAverage.addAverage(starts, allJoins.size, uniqueJoins.size)
            }

            //Sort players according to most joins
            playerUniqueJoins.toSortedMap { o1, o2 ->
                if (playerUniqueJoins.getOrDefault(
                        o1,
                        0
                    ) > playerUniqueJoins.getOrDefault(o2, 0)
                ) 1 else if (playerUniqueJoins.getOrDefault(o1, 0) < playerUniqueJoins.getOrDefault(o2, 0)) -1 else 0
            }

            //Cleanup player calculations
            val playerUniqueJoinsArray: Array<out UUID> = playerUniqueJoins.keys.stream().toArray() as Array<out UUID>
            val topPlayers = arrayOf(if (playerUniqueJoinsArray.isNotEmpty()) playerUniqueJoinsArray[0] else null, if (playerUniqueJoinsArray.size > 1) playerUniqueJoinsArray[1] else null, if (playerUniqueJoinsArray.size > 2) playerUniqueJoinsArray[2] else null)
            val uniquePlayers = playerUniqueJoins.keys
            val playerAverage = uniquePlayers.size / dates.size


            for (day in weekAverage) {
                day.calculateAverage()
            }

            //Retrieve the players top servers according to laby.net
            val topServersHashMap = hashMapOf<LabyServer, Int>()
            val client = OkHttpClient()
            for (player in uniquePlayers) {
                val server = requestTopServer(player, client) ?: continue
                topServersHashMap[server] = topServersHashMap.getOrDefault(server, 0) + 1
            }

            //Cleanup top server calculations
            val topServersArray: Array<out LabyServer> = topServersHashMap.keys.stream().toArray() as Array<out LabyServer>
            val topServers = arrayOf(if (topServersArray.isNotEmpty()) topServersArray[0] else null, if (topServersArray.size > 1) topServersArray[1] else null, if (topServersArray.size > 2) topServersArray[2] else null)

            return Overview(ServerPersonality.NEWCOMER, installDate, startedServers, playerRecord, uniquePlayers.size, playerAverage, playerRecord, topPlayers, topServers, joins, weekAverage)
        }


        private fun requestTopServer(uuid: UUID, client: OkHttpClient) : LabyServer? {
            val request = Request.Builder()
                .url(USER_STATS_FORMAT.format("%s", uuid.toString()))
                .get()
                .build()
            val response = client.newCall(request).execute()
            if(response.body == null || response.code != 200) return null
            val body = response.body!!.string()
            val bodyJson = JsonParser.parseString(body).asJsonObject
            if(!bodyJson.has("most_played_server")) return null
            val serverObject = bodyJson.getAsJsonObject("most_played_server")
            if(!serverObject.has("nice_name") || !serverObject.has("direct_ip")) return null
            return LabyServer(serverObject.get("nice_name").asString, serverObject.get("direct_ip").asString)
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