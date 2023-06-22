package eu.thesimplecloud.module.npc.lib.config

import eu.thesimplecloud.module.npc.lib.config.npc.CloudNPCData
import eu.thesimplecloud.plugin.startup.CloudPlugin

data class NPCsConfig(
    val npcs: MutableList<CloudNPCData>,
) {

    fun getNpcsFromTargetGroup(): List<CloudNPCData> {
        val groupName = CloudPlugin.instance.thisService().getGroupName()
        return this.npcs.filter { it.targetGroup == groupName }
    }

}