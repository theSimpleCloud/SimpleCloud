package eu.thesimplecloud.module.npc.plugin.npc.type

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.module.npc.lib.config.npc.CloudNPCData
import eu.thesimplecloud.module.npc.lib.config.npc.action.Action
import eu.thesimplecloud.module.npc.lib.extension.translateColorCodesFromString
import eu.thesimplecloud.module.npc.plugin.NPCPlugin
import eu.thesimplecloud.module.npc.plugin.npc.ServerNPCHandler
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import eu.thesimplecloud.plugin.extension.syncBukkit
import eu.thesimplecloud.plugin.server.CloudSpigotPlugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

abstract class AbstractServerNPC(
    val serverNPCHandler: ServerNPCHandler,
    val config: CloudNPCData
): ServerNPC {

    lateinit var npcLocation: Location

    private val holograms: MutableList<ArmorStand> = mutableListOf()

    fun spawnHolograms() {
        val locationData = this.config.locationData

        val location = Location(Bukkit.getWorld(locationData.world), locationData.x, (locationData.y + this.getEntityHigh()) - 0.3, locationData.z)

        this.config.lines.forEach { _ ->
            val armorStand = location.world?.spawn(location.add(0.0, 0.3, 0.0), ArmorStand::class.java)
            armorStand?.setGravity(false)
            armorStand?.isMarker = true
            armorStand?.isInvulnerable = true
            armorStand?.isCustomNameVisible = true
            armorStand?.customName = "Loading"
            armorStand?.isVisible = false

            if (armorStand != null) {
                this.holograms.add(armorStand)
            } else {
                println("Can not spawn hologram for the npc ${this.config.id}")
            }
        }
        val serviceGroupByName =
            CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(this.config.targetGroup) ?: return
        this.updateHolograms(serviceGroupByName)
    }

    fun updateHolograms(group: ICloudServiceGroup) {
        var i = 0
        this.holograms.forEach { armorStand ->
            armorStand.customName = this.config.lines[i]
                .replace("%PLAYERS_ONLINE%", "${group.getAllServices().sumOf { it.getOnlineCount() }}")
                .replace("%SERVICES_ONLINE%", "${group.getOnlineServiceCount()}")
                .replace("%DISPLAYNAME%", this.config.displayName)
                .replace("%GROUP%", this.config.targetGroup)
                .replace("%TEAMPLATE%", group.getTemplateName())
                .translateColorCodesFromString()
            i++
        }
    }

    fun deleteHolograms() {
        this.holograms.forEach {
            it.remove()
        }
    }

    fun handlingInteract(player: Player, rightClicked: Boolean) {
        val action = this.config.npcAction
        if (rightClicked) {
            handleClickInteract(player, action.rightClick)
        } else {
            handleClickInteract(player, action.leftClick)
        }
    }

    private fun handleClickInteract(player: Player, clickAction: Action){
        when (clickAction) {
            Action.QUICK_JOIN -> handleQuickJoin(player)
            Action.OPEN_INVENTORY -> handleInventory(player)
            Action.RUN_COMMAND -> handleRunCommand(player)
        }
    }

    private fun handleRunCommand(player: Player) {
        val runCommandName = this.config.npcSettings.mobNPCSettings.runCommandName ?: return
        Bukkit.getScheduler().runTask(NPCPlugin.instance, kotlinx.coroutines.Runnable {
            Bukkit.dispatchCommand(player, runCommandName)
        })
    }

    private fun handleQuickJoin(player: Player) {
        val serviceGroup =
            CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(this.config.targetGroup) ?: return

        val iCloudServiceList =
            serviceGroup.getAllServices()
                .filter { it.getState() == ServiceState.VISIBLE }
                .filter { !it.isFull() }
                .sortedBy { it.getOnlineCount() }

        val iCloudService = iCloudServiceList.firstOrNull()
        val cloudPlayer = player.getCloudPlayer()

        if (iCloudService == null) {
            cloudPlayer.sendProperty("service.interact.no.free.service.found")
            return
        }

        cloudPlayer.connect(iCloudService)
    }

    private fun handleInventory(player: Player) {
        Bukkit.getScheduler().runTask(this.serverNPCHandler.npcPlugin, Runnable {
            NPCPlugin.instance.inventoryHandler.open(player, this.config.targetGroup)
        })
    }

}