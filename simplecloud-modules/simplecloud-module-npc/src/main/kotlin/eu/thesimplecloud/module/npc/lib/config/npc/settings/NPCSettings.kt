package eu.thesimplecloud.module.npc.lib.config.npc.settings

data class NPCSettings(
    var glowing: Boolean = false,
    var onFire: Boolean = false,
    val mobNPCSettings: MobNPCSettings,
    val playerNPCData: PlayerNPCSettings
)
