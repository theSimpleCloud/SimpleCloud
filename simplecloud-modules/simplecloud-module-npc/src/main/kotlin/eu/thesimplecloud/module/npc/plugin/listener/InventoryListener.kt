package eu.thesimplecloud.module.npc.plugin.listener

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.module.npc.plugin.NPCPlugin
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryInteractEvent

class InventoryListener(
    private val npcPlugin: NPCPlugin
): Listener {

    @EventHandler
    fun onClick(event: InventoryInteractEvent) {
        val inventory = event.inventory
        this.npcPlugin.inventoryHandler.inventories.find { it.inventory == inventory } ?: return
        event.isCancelled = true
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val inventory = event.inventory
        val player = event.whoClicked as Player
        val inventoryData = this.npcPlugin.inventoryHandler.inventories.find { it.inventory == inventory } ?: return

        event.isCancelled = true
        val currentItem = event.currentItem ?: return

        val service = inventoryData.items[currentItem] ?: return
        val iCloudService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(service)
        val cloudPlayer = player.getCloudPlayer()

        if (iCloudService == null) {
            cloudPlayer.sendProperty("service.interact.service.not.available")
            return
        }

        if (iCloudService.getState() == ServiceState.INVISIBLE) {
            cloudPlayer.sendProperty("service.interact.service.in.game")
            return
        }

        cloudPlayer.connect(iCloudService)
    }
}