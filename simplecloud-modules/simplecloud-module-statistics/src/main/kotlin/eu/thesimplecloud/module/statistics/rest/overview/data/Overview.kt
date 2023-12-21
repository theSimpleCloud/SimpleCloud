package eu.thesimplecloud.module.statistics.rest.overview.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.module.statistics.StatisticsModule
import eu.thesimplecloud.module.statistics.timed.TimedValue
import eu.thesimplecloud.module.statistics.timed.store.ITimedValueStore
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileWriter
import java.util.*


data class Overview(
    var personality: ServerPersonality = ServerPersonality.DEVELOPING_NETWORK,
    val installDate: Long,
    val startedServers: Int,
    val averageStartedServers: Int,
    val startedServerRecord: TimedValue<Int>,
    val players: Int,
    val playerAverage: Int,
    val playerRecord: TimedValue<Int>,
    val topPlayers: SortedSet<LabyPlayer>,
    val topFavoriteServer: LabyServer?,
    val joins: Int,
    val averageScore: Int,
    val averageScoreName: String,
    val averageScoreColor: String,
    val weekAverage: Array<DayAverage>,
) {

    companion object {

        val client = OkHttpClient()
        private const val USER_STATS_FORMAT = "https://laby.net/api/v3/user/%s/game-stats"
        fun create(force: Boolean): Overview {

            if (!force) {
                val saved = get()
                if (saved != null) return saved
            }

            val playerJoins: ITimedValueStore<*> =
                (StatisticsModule.instance.getValueStoreByName("cloud_stats_player_connects"))!!
            val joins = playerJoins.count()

            val serverStarts: ITimedValueStore<*> =
                (StatisticsModule.instance.getValueStoreByName("cloud_stats_service_starts"))!!
            val startedServers = serverStarts.count()

            var playerRecord = TimedValue(-1)
            var serversRecord = TimedValue(-1)

            val currentYear: Long = GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), 0, 0).timeInMillis
            val currentTime = System.currentTimeMillis()

            val calender = Calendar.getInstance()
            val allStarts = serverStarts.get(0, currentTime)
            var installDate = if (allStarts.isNotEmpty()) serverStarts.get(
                0,
                currentTime
            )[0].getTimeStamp() else System.currentTimeMillis()
            calender.time = Date(installDate)
            installDate = calender.get(Calendar.DAY_OF_YEAR) * 24L * 60L * 60 * 1000L + currentYear
            val dates = getDates(currentYear.coerceAtLeast(installDate), currentTime)
            val averageStartedServers = startedServers / dates.size


            val playerUniqueJoins = hashMapOf<UUID, Int>()
            val weekAverage = arrayOf(
                DayAverage.empty(),
                DayAverage.empty(),
                DayAverage.empty(),
                DayAverage.empty(),
                DayAverage.empty(),
                DayAverage.empty(),
                DayAverage.empty()
            )
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
                    playerUniqueJoins[UUID.fromString(join.value.toString())] =
                        playerUniqueJoins.getOrDefault(join.value, 0) + 1
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
            val sortedUniquePlayers = playerUniqueJoins.toSortedMap(
                compareBy<UUID> { -playerUniqueJoins.getOrDefault(it, 0) }.thenBy { it }
            )

            //Cleanup player calculations
            val playerUniqueJoinsArray: Array<out UUID> = playerUniqueJoins.keys.toTypedArray()
            val uniquePlayers = playerUniqueJoinsArray.size

            val playerAverage = averageUniqueJoins / if (dates.isNotEmpty()) dates.size else 1
            val topPlayers = hashMapOf<UUID, Int>()
            val sortedPlayersIterator = sortedUniquePlayers.keys.iterator()
            for (maxTopPlayers in 0..4) {
                if (!sortedPlayersIterator.hasNext()) {
                    continue
                }
                if (sortedUniquePlayers.size > maxTopPlayers) {
                    val key = sortedPlayersIterator.next()
                    topPlayers[key] = playerUniqueJoins.getOrDefault(key, -1)
                }
            }
            val topPlayersSorted = topPlayers.toSortedMap(
                compareBy<UUID> { -topPlayers.getOrDefault(it, 0) }.thenBy { it }
            )

            val topLabyPlayers = mutableListOf<LabyPlayer>()
            topPlayersSorted.keys.toList().forEach { uuid ->
                val player = LabyPlayer()
                player.createRequestURL(uuid)
                player.retrieve(client)
                player.joins = topPlayersSorted.getOrDefault(uuid, 0)
                topLabyPlayers.add(player)
            }
            val topLabyPlayersSorted = topLabyPlayers.toSortedSet(
                compareBy<LabyPlayer> { -it.joins }.thenBy { it.name }
            )


            //Retrieve the players top servers according to laby.net
            val topServersHashMap = hashMapOf<LabyServer, Int>()
            for (player in playerUniqueJoinsArray) {
                val server = LabyServer()
                server.createRequestURL(player)
                if (!server.retrieve(client)) continue
                topServersHashMap[server] = topServersHashMap.getOrDefault(server, 0) + 1
            }

            //Cleanup top server calculations
            val topServersArray: Array<out LabyServer> = topServersHashMap.keys.toTypedArray()
            val topServer = if (topServersArray.isNotEmpty()) topServersArray[0] else null

            for (average in weekAverage) {
                average.calculateAverage()
            }

            var scoreAverage = 0
            weekAverage.forEach { average ->
                scoreAverage += average.score
            }
            scoreAverage /= weekAverage.size
            var scoreAverageName = ""
            var scoreAverageColor = ""
            if (scoreAverage > 375) {
                scoreAverageName = "Insane"
                scoreAverageColor = "#1c4d78"
            } else if (scoreAverage > 200) {
                scoreAverageName = "Epic"
                scoreAverageColor = "#781c73"
            } else if (scoreAverage > 100) {
                scoreAverageName = "Good"
                scoreAverageColor = "#106633"
            } else if (scoreAverage > 50) {
                scoreAverageName = "Average"
                scoreAverageColor = "#995a08"
            } else {
                scoreAverageName = "Casual"
                scoreAverageColor = "#474747"
            }

            //TODO: Add ServerPersonality detection
            val generatedOverview = Overview(
                ServerPersonality.DEVELOPING_NETWORK,
                installDate,
                startedServers,
                averageStartedServers,
                playerRecord,
                uniquePlayers,
                playerAverage,
                playerRecord,
                topLabyPlayersSorted,
                topServer,
                joins,
                scoreAverage,
                scoreAverageName,
                scoreAverageColor,
                weekAverage
            )
            generatedOverview.personality = ServerPersonality.calculate(generatedOverview)
            save(generatedOverview)
            return generatedOverview
        }

        private fun save(overview: Overview) {
            val file = File("modules/statistics/overview_latest.json")
            if (!file.exists()) {
                if (!file.parentFile.exists()) file.parentFile.mkdirs()
                file.createNewFile()
            }
            val gson = GsonBuilder().setPrettyPrinting().create()
            val writer = FileWriter(file)
            gson.toJson(overview, writer)
            writer.close()
        }

        private fun get(): Overview? {
            try {
                val file = File("modules/statistics/overview_latest.json")
                if (!file.exists()) return null
                val gson = Gson()
                val overview = JsonLib.fromJsonFile(file, gson)!!.getObject(Overview::class.java)
                return overview
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
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