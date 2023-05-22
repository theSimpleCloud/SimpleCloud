package eu.thesimplecloud.module.npc.lib.config

data class InventorySettingsConfig(
    val inventory: MutableMap<Int, String>,
    val rows: Int = 5,
    val inventoryName: String = "§8» §7%TARGET_GROUP% §8«",
    val onlineService: String = "GREEN_BANNER",
    val fullService: String = "ORANGE_BANNER",
    val itemName: String = "§8» §7%SERVICE_NAME%",
    val lore: List<String> = listOf("§a%ONLINE_PLAYERS%§8/§2%MAX_PLAYERS% §7Players", "§7%MOTD%")
)