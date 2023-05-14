package eu.thesimplecloud.module.npc.plugin.inventory

import com.cryptomorin.xseries.XMaterial
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.module.npc.lib.config.NPCModuleConfig
import eu.thesimplecloud.module.npc.lib.extension.translateColorCodesFromString
import eu.thesimplecloud.module.npc.plugin.NPCPlugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.TimeUnit

class InventoryHandler(
    private val npcPlugin: NPCPlugin
) {

    val cache: MutableMap<UUID, Long> = mutableMapOf()
    val inventorys: MutableList<InventoryData> = mutableListOf()

    fun open(player: Player, group: String) {
        if (this.cache.contains(player.uniqueId)) {
            if (this.cache[player.uniqueId]!! > System.currentTimeMillis())
                return
        }
        this.cache[player.uniqueId] = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1)

        val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(group)!!
        val config = this.npcPlugin.serverNPCHandler!!.config
        val inventory = Bukkit.createInventory(player,
            9 * config.inventorySettingsConfig.rows,
            config.inventorySettingsConfig.inventoryName
                .replace("%TARGET_GROUP%", serviceGroup.getName())
                .replace("%TEMPLATE_NAME%", serviceGroup.getTemplateName())
                .replace("%MAX_PLAYERS%", "${serviceGroup.getMaxPlayers()}")
                .translateColorCodesFromString())
        val inventoryData = InventoryData(group, inventory)

        config.inventorySettingsConfig.inventory.forEach { (slot, material) ->
            val xMaterial = XMaterial.valueOf(material)
            val itemStack = xMaterial.parseItem()
            if (itemStack != null) {
                val itemMeta = itemStack.itemMeta!!
                itemMeta.setDisplayName(" ")
                itemStack.itemMeta = itemMeta
                inventory.setItem(slot, itemStack)
            }
        }

        serviceGroup.getAllServices().filter { it.isOnline() }.forEach { service ->
            val itemStack = ItemStack(this.getMaterial(service, config))
            val itemMeta = itemStack.itemMeta!!
            itemMeta.setDisplayName(config.inventorySettingsConfig.itemName
                .replace("%SERVICE_NAME%", service.getName())
                .replace("%TEMPLATE_NAME%", service.getTemplateName())
                .replace("%WRAPPER_NAME%", service.getWrapper().getName())
                .replace("%DISPLAYNAME%", service.getDisplayName())
                .replace("%ONLINE_PLAYERS%", "${service.getOnlineCount()}")
                .replace("%HOST%", service.getHost())
                .replace("%PORT%", "${service.getPort()}")
                .replace("%STATE%", service.getState().name)
                .replace("%NUMBER%", "${service.getServiceNumber()}")
                .replace("%MAX_PLAYERS%", "${service.getMaxPlayers()}")
                .replace("%MOTD%", service.getMOTD())
                .translateColorCodesFromString())

            config.inventorySettingsConfig.lore
                .forEach {
                    val lore = itemMeta.lore ?: mutableListOf()
                    lore.add(it
                        .replace("%SERVICE_NAME%", service.getName())
                        .replace("%TEMPLATE_NAME%", service.getTemplateName())
                        .replace("%WRAPPER_NAME%", service.getWrapper().getName())
                        .replace("%DISPLAYNAME%", service.getDisplayName())
                        .replace("%ONLINE_PLAYERS%", "${service.getOnlineCount()}")
                        .replace("%HOST%", service.getHost())
                        .replace("%PORT%", "${service.getPort()}")
                        .replace("%STATE%", service.getState().name)
                        .replace("%NUMBER%", "${service.getServiceNumber()}")
                        .replace("%MAX_PLAYERS%", "${service.getMaxPlayers()}")
                        .replace("%MOTD%", service.getMOTD())
                        .translateColorCodesFromString()
                    )
                    itemMeta.lore = lore
                }
            itemStack.itemMeta = itemMeta
            inventory.addItem(itemStack)
            inventoryData.items[itemStack] = service.getName()
        }

        player.openInventory(inventory)
        player.playSound(player.location, Sound.BLOCK_CHEST_OPEN, 0.8F, 0.8F)
        this.inventorys.add(inventoryData)
    }

    private fun getMaterial(service: ICloudService, config: NPCModuleConfig): Material {
        return if (service.isFull()) {
            XMaterial.valueOf(config.inventorySettingsConfig.fullService).parseMaterial()!!
        } else {
            XMaterial.valueOf(config.inventorySettingsConfig.onlineService).parseMaterial()!!
        }
    }
}