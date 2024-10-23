package eu.thesimplecloud.module.statistics.rest.overview.data

import java.util.*
import kotlin.math.abs

enum class ServerPersonality {
    WORKAHOLIC,
    EVERY_DAY_EVERY_NIGHT,
    MAYFLY,
    FRIDAY_CLUB,
    STARTUP_NETWORK,
    DEVELOPING_NETWORK,
    DISHWASHER,
    ENJOYABLE,
    NEWCOMER,
    SLOTH,
    OLD_STAGER;

    companion object {
        fun calculate(overview: Overview): ServerPersonality {
            var firstAverage = -1
            var isEveryDayEveryNight = true
            if (overview.averageScore == 0) isEveryDayEveryNight = false
            else overview.weekAverage.forEach { average ->
                if (firstAverage == -1) firstAverage = average.score
                if (abs(firstAverage - average.score) > firstAverage / 5) {
                    isEveryDayEveryNight = false
                    return@forEach
                }
            }
            if (isEveryDayEveryNight) return EVERY_DAY_EVERY_NIGHT
            var highestScore = 0
            overview.weekAverage.forEach { average ->
                if (highestScore < average.score) highestScore = average.score
            }
            val fridayScore = overview.weekAverage[5].score
            if (fridayScore >= highestScore && fridayScore >= overview.averageScore + (overview.averageScore / 4)) return FRIDAY_CLUB
            if (highestScore > (overview.averageScore + overview.averageScore / 3)) return MAYFLY
            var underWeekAverage = 0
            overview.weekAverage.forEach { average ->
                val weekIndex = overview.weekAverage.indexOf(average)
                if (weekIndex in 1..5) {
                    underWeekAverage += average.players
                }
            }
            underWeekAverage /= 5
            if (overview.averageScore < underWeekAverage) return WORKAHOLIC
            if (overview.players >= 10 && overview.players < overview.averageStartedServers) return STARTUP_NETWORK
            if (overview.players * 10 < overview.startedServers) return DISHWASHER
            var averageJoins = 0
            var averagePlayers = 0
            overview.weekAverage.forEach { average ->
                averageJoins += average.playerJoins
                averagePlayers += average.players
            }
            if (averagePlayers + averagePlayers / 6 >= averageJoins && overview.averageScore > 0) return ENJOYABLE
            if (overview.averageStartedServers < 3) return SLOTH
            if (overview.installDate - System.currentTimeMillis() <= 14L * 24L * 60L * 60L * 1000L) return NEWCOMER
            val cal = Calendar.getInstance()
            cal.time = Date(overview.installDate)
            val cal2 = Calendar.getInstance()
            cal2.time = Date(System.currentTimeMillis())
            if (cal.get(Calendar.YEAR) != cal2.get(Calendar.YEAR)) return OLD_STAGER

            return DEVELOPING_NETWORK
        }
    }
}