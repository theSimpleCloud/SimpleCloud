package eu.thesimplecloud.module.npc.plugin.listener

import eu.thesimplecloud.module.npc.plugin.NPCPlugin
import eu.thesimplecloud.module.npc.plugin.npc.type.PlayerNPC
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerToggleSneakEvent

class PlayerConnectionListener(
    private val npcPlugin: NPCPlugin
): Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        this.npcPlugin.serverNPCHandler!!.serverNPC.forEach { (_, serverNPC) ->
            if (!serverNPC.config.isMob) {
                val playerNPC: PlayerNPC = serverNPC as PlayerNPC

                Bukkit.getOnlinePlayers().forEach {
                    this.npcPlugin.serverNPCHandler!!.updateScoreboardTeam(it.scoreboard, playerNPC.npc.profile().name())
                }
            }
        }
    }

    @EventHandler
    fun onToggleSneak(event: PlayerToggleSneakEvent) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.npcPlugin,
            Runnable {
                this.npcPlugin.serverNPCHandler!!.serverNPC.forEach { (_, serverNPC) ->
                    run {
                        if (!serverNPC.config.isMob) {
                            val playerNPC: PlayerNPC = serverNPC as PlayerNPC
                            playerNPC.updateNPCStatus()
                        }
                    }
                }
                     }, 1, 1
        )
    }
}