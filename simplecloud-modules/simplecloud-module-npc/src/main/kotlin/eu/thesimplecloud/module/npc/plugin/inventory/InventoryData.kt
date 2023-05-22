package eu.thesimplecloud.module.npc.plugin.inventory

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

data class InventoryData(
    val group: String,
    val inventory: Inventory,
    val items: MutableMap<ItemStack, String> = mutableMapOf()
)