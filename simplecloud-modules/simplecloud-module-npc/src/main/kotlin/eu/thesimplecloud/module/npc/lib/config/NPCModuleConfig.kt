package eu.thesimplecloud.module.npc.lib.config

import eu.thesimplecloud.api.CloudAPI

class NPCModuleConfig(
    val npcsConfig: NPCsConfig,
    val inventorySettingsConfig: InventorySettingsConfig
) {
    fun update() {
        CloudAPI.instance.getGlobalPropertyHolder().setProperty("npc-config", this)
    }
}