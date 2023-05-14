package eu.thesimplecloud.module.npc.lib.config.npc

import eu.thesimplecloud.module.npc.lib.config.npc.action.NPCAction
import eu.thesimplecloud.module.npc.lib.config.npc.settings.NPCSettings

data class CloudNPCData(
    var displayName: String,
    val id: String,
    val isMob: Boolean,
    var targetGroup: String,
    var locationData: LocationData,
    val npcAction: NPCAction,
    val npcItem: NPCItem,
    val npcSettings: NPCSettings,
    var lines: List<String>
)