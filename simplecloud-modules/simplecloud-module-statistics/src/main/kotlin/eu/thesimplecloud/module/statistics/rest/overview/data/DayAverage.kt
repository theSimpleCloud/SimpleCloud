package eu.thesimplecloud.module.statistics.rest.overview.data

data class DayAverage(
    var serverStarts: Int,
    var playerJoins: Int,
    var players: Int,
) {
    private var totalDays = 0
    fun addAverage(serverStarts: Int, playerJoins: Int, players: Int)
    {
        this.serverStarts += serverStarts
        this.players += players
        this.playerJoins += playerJoins
        totalDays++
    }

    fun calculateAverage()
    {
        this.serverStarts /= totalDays
        this.playerJoins /= totalDays
        this.players /= totalDays
    }

    companion object {
        fun empty() : DayAverage {
            return DayAverage(-1, -1, -1)
        }
    }
}