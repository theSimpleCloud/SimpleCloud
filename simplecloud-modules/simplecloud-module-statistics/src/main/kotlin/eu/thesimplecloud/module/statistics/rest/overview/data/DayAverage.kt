package eu.thesimplecloud.module.statistics.rest.overview.data

data class DayAverage(
    var serverStarts: Int,
    var playerJoins: Int,
    var players: Int,
    var score: Int,
) {
    private var totalDays = 0
    fun addAverage(serverStarts: Int, playerJoins: Int, players: Int) {
        this.serverStarts += serverStarts
        this.players += players
        this.playerJoins += playerJoins
        this.score += serverStarts + playerJoins + players * 2
        totalDays++
    }

    fun calculateAverage() {
        this.serverStarts /= if (totalDays > 0) totalDays else 1
        this.playerJoins /= if (totalDays > 0) totalDays else 1
        this.players /= if (totalDays > 0) totalDays else 1
        this.score /= if (totalDays > 0) totalDays else 1
    }

    companion object {
        fun empty(): DayAverage {
            return DayAverage(0, 0, 0, 0)
        }
    }
}