package eu.thesimplecloud.module.npc.lib.config

import eu.thesimplecloud.module.npc.lib.config.npc.CloudNPCData
import eu.thesimplecloud.plugin.startup.CloudPlugin

data class NPCsConfig(
    val npcs: MutableList<CloudNPCData>,
) {

    fun existNpcWithId(id: String): Boolean {
        return this.npcs.firstOrNull { it.id == id } != null
    }

}