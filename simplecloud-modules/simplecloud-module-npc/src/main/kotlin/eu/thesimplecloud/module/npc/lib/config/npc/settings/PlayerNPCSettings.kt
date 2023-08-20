package eu.thesimplecloud.module.npc.lib.config.npc.settings

import eu.thesimplecloud.module.npc.lib.config.npc.SkinData

data class PlayerNPCSettings(
    var skinData: SkinData,
    var flyingWithElytra: Boolean = false,
    var lookAtPlayer: Boolean = true,
    var hitWhenPlayerHits: Boolean = false,
    var sneakWhenPlayerSneaks: Boolean = false
)
