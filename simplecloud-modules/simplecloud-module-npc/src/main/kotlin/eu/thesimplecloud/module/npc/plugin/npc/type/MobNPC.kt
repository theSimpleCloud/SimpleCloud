package eu.thesimplecloud.module.npc.plugin.npc.type

import com.cryptomorin.xseries.XMaterial
import eu.thesimplecloud.module.npc.lib.config.npc.CloudNPCData
import eu.thesimplecloud.module.npc.plugin.NPCPlugin
import eu.thesimplecloud.module.npc.plugin.npc.ServerNPCHandler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.inventory.EquipmentSlot
import java.util.*

class MobNPC(
    serverNPCHandler: ServerNPCHandler,
    config: CloudNPCData
) : AbstractServerNPC(serverNPCHandler, config), Listener {

    private lateinit var entity: Entity
    private lateinit var uniqueId: UUID

    init {
        Bukkit.getPluginManager().registerEvents(this, NPCPlugin.instance)
    }

    override fun onSetup() {
        val location = this.config.locationData
        this.npcLocation = Location(Bukkit.getWorld(location.world), location.x, location.y, location.z, location.yaw, location.pitch)
        this.entity = this.npcLocation.world?.spawnEntity(this.npcLocation, EntityType.valueOf(this.config.npcSettings.mobNPCSettings.mobType))!!

        this.entity.setGravity(false)
        this.entity.isCustomNameVisible = false
        this.entity.isSilent = true
        if (this.entity is LivingEntity)
            (this.entity as LivingEntity).setAI(false)

        this.uniqueId = this.entity.uniqueId

        this.entity.isGlowing = this.config.npcSettings.glowing

        this.entity.isVisualFire = this.config.npcSettings.onFire

        this.handleItemForEntity()
    }

    override fun onRemove() {
        this.entity.remove()
    }

    private fun handleItemForEntity() {
        if (this.entity is LivingEntity) {
            (this.entity as LivingEntity).equipment?.setItem(
                EquipmentSlot.HAND,
                this.config.npcItem.rightHand?.let { XMaterial.valueOf(it).parseItem() })
        }
        if (this.entity is LivingEntity) {
            (this.entity as LivingEntity).equipment?.setItem(
                EquipmentSlot.OFF_HAND,
                this.config.npcItem.leftHand?.let { XMaterial.valueOf(it).parseItem() })
        }
    }

    @EventHandler
    fun onPlayerInteractAtEntity(event: PlayerInteractAtEntityEvent) {
        val player = event.player
        val rightClicked = event.rightClicked

        if (rightClicked.uniqueId != this.uniqueId)
            return

        event.isCancelled = true
        this.handlingInteract(player, true)
    }

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val damage = event.damager
        val entity = event.entity

        if (this.uniqueId != entity.uniqueId)
            return

        if (damage !is Player)
            return

        event.isCancelled = true
        this.handlingInteract(damage, false)
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        val uuid = event.entity.uniqueId

        if (this.uniqueId == uuid) {
            event.isCancelled = true
            return
        }
    }

    @EventHandler
    fun onVehicleEnter(event: VehicleEnterEvent) {
        val uuid = event.vehicle.uniqueId

        if (this.uniqueId == uuid) {
            event.isCancelled = true
            return
        }
    }

    override fun getEntityHigh(): Double {
        return this.entity.height
    }
}

