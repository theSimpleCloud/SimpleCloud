package eu.thesimplecloud.module.npc.plugin.npc

import com.github.juliarn.npclib.api.Platform
import com.github.juliarn.npclib.bukkit.BukkitPlatform
import com.github.juliarn.npclib.bukkit.BukkitWorldAccessor
import eu.thesimplecloud.module.npc.lib.config.NPCModuleConfig
import eu.thesimplecloud.module.npc.plugin.NPCPlugin
import eu.thesimplecloud.module.npc.plugin.npc.type.AbstractServerNPC
import eu.thesimplecloud.module.npc.plugin.npc.type.MobNPC
import eu.thesimplecloud.module.npc.plugin.npc.type.PlayerNPC
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

class ServerNPCHandler(
    val npcPlugin: NPCPlugin
) {

    lateinit var config: NPCModuleConfig

    val platform: Platform<World, Player, ItemStack, Plugin> = BukkitPlatform.bukkitNpcPlatformBuilder()
        .extension(this.npcPlugin)
        .debug(true)
        .worldAccessor(BukkitWorldAccessor.nameBasedAccessor())
        .actionController { }
        .build()

    val serverNPC: MutableMap<String, AbstractServerNPC> = mutableMapOf()

    fun createNPCs() {
        config.npcsConfig.npcs
            .filter { isRightService(it.createdInThisGroup) }
            .forEach { npcInformation ->
            val npc = if (npcInformation.isMob) {
                MobNPC(this, npcInformation)
            } else {
                PlayerNPC(this, npcInformation)
            }

            npc.onSetup()
            npc.spawnHolograms()

            this.serverNPC[npcInformation.id] = npc

            if (!npcInformation.isMob) {
                val playerNPC = npc as PlayerNPC
                Bukkit.getOnlinePlayers().forEach {
                    updateScoreboardTeam(it.scoreboard, playerNPC.npc.profile().name())
                }
            }
        }
    }

    fun deleteNPCs() {
        this.serverNPC.forEach { (_, npc) ->
            run {
                npc.onRemove()
                npc.deleteHolograms()
            }
        }
        this.serverNPC.clear()
    }

    fun updateScoreboardTeam(scoreboard: Scoreboard, npcName: String) {
        var team = scoreboard.getTeam("simpleCloudNpc")
            ?: scoreboard.registerNewTeam("simpleCloudNpc")
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)
        team.addEntry(npcName)
    }

    private fun isRightService(serviceName: String?): Boolean {
        if (serviceName == null)
            return true
        val thisService = CloudPlugin.instance.thisService()
        return thisService.getName() == serviceName
                || thisService.getGroupName() == serviceName
    }
}