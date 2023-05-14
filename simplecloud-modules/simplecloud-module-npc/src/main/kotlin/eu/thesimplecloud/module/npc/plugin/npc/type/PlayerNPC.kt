package eu.thesimplecloud.module.npc.plugin.npc.type

import com.cryptomorin.xseries.XMaterial
import com.github.juliarn.npclib.api.Npc
import com.github.juliarn.npclib.api.event.InteractNpcEvent
import com.github.juliarn.npclib.api.event.ShowNpcEvent
import com.github.juliarn.npclib.api.profile.Profile
import com.github.juliarn.npclib.api.profile.ProfileProperty
import com.github.juliarn.npclib.api.protocol.enums.EntityStatus
import com.github.juliarn.npclib.api.protocol.enums.ItemSlot
import com.github.juliarn.npclib.api.protocol.meta.EntityMetadataFactory
import com.github.juliarn.npclib.bukkit.util.BukkitPlatformUtil
import com.github.juliarn.npclib.common.event.DefaultAttackNpcEvent
import com.github.juliarn.npclib.common.event.DefaultInteractNpcEvent
import eu.thesimplecloud.module.npc.lib.config.npc.CloudNPCData
import eu.thesimplecloud.module.npc.plugin.npc.ServerNPCHandler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.util.*

class PlayerNPC(
    serverNPCHandler: ServerNPCHandler,
    config: CloudNPCData
) : AbstractServerNPC(serverNPCHandler, config) {

    lateinit var npc: Npc<World, Player, ItemStack, Plugin>

    override fun onSetup() {
        val location = this.config.locationData
        this.npcLocation = Location(Bukkit.getWorld(location.world), location.x, location.y, location.z, location.yaw, location.pitch)

        val position = BukkitPlatformUtil.positionFromBukkitLegacy(this.npcLocation)
        val textures = ProfileProperty.property(
            "textures",
            this.config.npcSettings.playerNPCData.skinData.value,
            this.config.npcSettings.playerNPCData.skinData.signature
        )

        this.npc = this.serverNPCHandler.platform.newNpcBuilder()
            .position(position)
            .flag(Npc.LOOK_AT_PLAYER, this.config.npcSettings.playerNPCData.lookAtPlayer)
            .flag(Npc.HIT_WHEN_PLAYER_HITS, this.config.npcSettings.playerNPCData.hitWhenPlayerHits)
            .flag(Npc.SNEAK_WHEN_PLAYER_SNEAKS, this.config.npcSettings.playerNPCData.sneakWhenPlayerSneaks)
            .profile(
                Profile.resolved(
                    "SP-NPC-" + Random().nextInt(9999999),
                    UUID.randomUUID(),
                    mutableSetOf(textures)
                )
            )
            .buildAndTrack()

        this.serverNPCHandler.platform.eventBus().subscribe(DefaultInteractNpcEvent::class.java, this::onDefaultInteractNpc)
        this.serverNPCHandler.platform.eventBus().subscribe(DefaultAttackNpcEvent::class.java, this::onDefaultAttackNpc)
        this.serverNPCHandler.platform.eventBus().subscribe(ShowNpcEvent.Post::class.java, this::onShowNpc)
    }

    override fun onRemove() {
        this.npc.unlink()
    }

    fun updateNPCStatus() {
        val entityStatuses: MutableList<EntityStatus> = ArrayList()
        if (this.config.npcSettings.glowing) entityStatuses.add(EntityStatus.GLOWING)
        if (this.config.npcSettings.playerNPCData.flyingWithElytra) entityStatuses.add(EntityStatus.FLYING_WITH_ELYTRA)
        if (this.config.npcSettings.onFire) entityStatuses.add(EntityStatus.ON_FIRE)
        val packetFactory = npc.platform().packetFactory()
        packetFactory.createEntityMetaPacket(
            entityStatuses,
            EntityMetadataFactory.entityStatusMetaFactory()
        ).scheduleForTracked(npc)
    }

    private fun onShowNpc(event: ShowNpcEvent.Post) {
        val packetFactory = npc.platform().packetFactory()
        packetFactory.createEntityMetaPacket(true, EntityMetadataFactory.skinLayerMetaFactory()).scheduleForTracked(npc)
        this.updateNPCStatus()

        if (config.npcItem.rightHand != null)
            packetFactory.createEquipmentPacket(ItemSlot.MAIN_HAND, ItemStack(XMaterial.valueOf(config.npcItem.rightHand!!).parseMaterial()!!))
                .scheduleForTracked(npc)

        if (config.npcItem.leftHand != null)
            packetFactory.createEquipmentPacket(ItemSlot.OFF_HAND, ItemStack(XMaterial.valueOf(config.npcItem.leftHand!!).parseMaterial()!!))
                .scheduleForTracked(npc)
    }

    private fun onDefaultInteractNpc(event: DefaultInteractNpcEvent) {
        val player = event.player<Player>()
        val targetNpc = event.npc<World, Player, ItemStack, Plugin>()

        if (event.hand() == InteractNpcEvent.Hand.OFF_HAND)
            return
        if (this.npc.entityId() != targetNpc.entityId())
            return

        this.handlingInteract(player, true)
    }

    private fun onDefaultAttackNpc(event: DefaultAttackNpcEvent) {
        val player = event.player<Player>()
        val targetNpc = event.npc<World, Player, ItemStack, Plugin>()

        if (this.npc.entityId() != targetNpc.entityId())
            return
        this.handlingInteract(player, false)
    }

    override fun getEntityHigh(): Double {
        return 1.85
    }
}