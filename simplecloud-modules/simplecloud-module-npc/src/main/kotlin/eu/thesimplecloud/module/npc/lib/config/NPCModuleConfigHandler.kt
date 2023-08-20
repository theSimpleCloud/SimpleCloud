package eu.thesimplecloud.module.npc.lib.config

import eu.thesimplecloud.jsonlib.JsonLib
import java.io.File

class NPCModuleConfigHandler {

    private val npcFile = File("modules/npc/npcs.json")
    private val inventoryFile = File("modules/npc/inventory.json")

    fun save(config: NPCModuleConfig) {
        JsonLib.fromObject(config.npcsConfig).saveAsFile(this.npcFile)
        JsonLib.fromObject(config.inventorySettingsConfig).saveAsFile(this.inventoryFile)
    }

    fun load(): NPCModuleConfig {
        if (!this.npcFile.exists()) return this.createConfig()
        val npCsConfig = JsonLib.fromJsonFile(this.npcFile)!!.getObject(NPCsConfig::class.java)
        val inventorySettingsConfig =
            JsonLib.fromJsonFile(this.inventoryFile)!!.getObject(InventorySettingsConfig::class.java)
        return NPCModuleConfig(npCsConfig, inventorySettingsConfig)
    }

    private fun createConfig(): NPCModuleConfig {
        return NPCModuleConfig(
            NPCsConfig(mutableListOf()), InventorySettingsConfig(
                mutableMapOf(
                    Pair(0, "BLACK_STAINED_GLASS_PANE"),
                    Pair(1, "BLACK_STAINED_GLASS_PANE"),
                    Pair(2, "BLACK_STAINED_GLASS_PANE"),
                    Pair(3, "BLACK_STAINED_GLASS_PANE"),
                    Pair(4, "BLACK_STAINED_GLASS_PANE"),
                    Pair(5, "BLACK_STAINED_GLASS_PANE"),
                    Pair(6, "BLACK_STAINED_GLASS_PANE"),
                    Pair(7, "BLACK_STAINED_GLASS_PANE"),
                    Pair(8, "BLACK_STAINED_GLASS_PANE"),

                    Pair(36, "BLACK_STAINED_GLASS_PANE"),
                    Pair(37, "BLACK_STAINED_GLASS_PANE"),
                    Pair(38, "BLACK_STAINED_GLASS_PANE"),
                    Pair(39, "BLACK_STAINED_GLASS_PANE"),
                    Pair(40, "BLACK_STAINED_GLASS_PANE"),
                    Pair(41, "BLACK_STAINED_GLASS_PANE"),
                    Pair(42, "BLACK_STAINED_GLASS_PANE"),
                    Pair(43, "BLACK_STAINED_GLASS_PANE"),
                    Pair(44, "BLACK_STAINED_GLASS_PANE")
                )
            ))
    }
}