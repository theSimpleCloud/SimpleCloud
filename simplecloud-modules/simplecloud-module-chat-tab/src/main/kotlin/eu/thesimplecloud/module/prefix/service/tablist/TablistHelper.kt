package eu.thesimplecloud.module.prefix.service.tablist

import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.prefix.config.Config
import eu.thesimplecloud.module.prefix.config.TablistInformation
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 19.12.2020
 * Time: 15:11
 */
object TablistHelper {

    fun load() {
        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard ?: return

        initScoreboard(scoreboard)

        Bukkit.getOnlinePlayers().forEach {
            updateScoreboardForPlayer(it)
        }
    }

    private fun initScoreboard(scoreboard: Scoreboard) {
        Config.getConfig().informationList.forEach {
            val team = scoreboard.getTeam(it.priority.toString()) ?: scoreboard.registerNewTeam(it.priority.toString())

            val chatColor = ChatColor.valueOf(it.color)

            team.prefix = ChatColor.translateAlternateColorCodes('&', it.prefix) + chatColor.toString()
            team.suffix = ChatColor.translateAlternateColorCodes('&', it.suffix)

            try {
                team.color = chatColor
            } catch (ex: NoSuchMethodException) {
            } catch (ex: NoSuchMethodError) {
            }
        }
    }

    fun updateScoreboardForAllPlayers() {
        Bukkit.getOnlinePlayers().forEach {
            updateScoreboardForPlayer(it)
        }
    }

    fun updateScoreboardForPlayer(player: Player) {
        val scoreboard = player.scoreboard
        initScoreboard(scoreboard)
        Bukkit.getOnlinePlayers().forEach { setPlayerInScoreboard(it, scoreboard) }
    }

    private fun setPlayerInScoreboard(player: Player, scoreboard: Scoreboard) {

        scoreboard.teams.forEach {
            it.removeEntry(player.name)
        }

        val tablistInformation = getTablistInformationByPlayer(player) ?: return
        val teamName = tablistInformation.priority.toString()
        val team = scoreboard.getTeam(teamName) ?: return

        team.addEntry(player.name)
    }

    fun getTablistInformationByPlayer(player: Player): TablistInformation? {
        val permissionPlayer =
                PermissionPool.instance.getPermissionPlayerManager().getCachedPermissionPlayer(player.uniqueId)
                        ?: return null

        val informationList = Config.getConfig().informationList
        val tablistInformation = informationList.sortedBy { it.priority }.first {
            permissionPlayer.hasPermissionGroup(it.groupName)
        }

        return tablistInformation
    }

}