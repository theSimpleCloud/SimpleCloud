package eu.thesimplecloud.module.npc.plugin.listener

import eu.thesimplecloud.module.npc.plugin.NPCPlugin
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent

class EntityListener(
    private val npcPlugin: NPCPlugin
): Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
        fun onSpawn(event: EntitySpawnEvent) {
        if (event.entityType != EntityType.ARMOR_STAND)
            return

        if (!event.isCancelled)
            return

        val location = event.location
        val locations = this.npcPlugin.serverNPCHandler?.serverNPC?.map { it.value.npcLocation }

        val find = locations?.find { it.blockX == location.blockX && it.blockZ == location.blockZ }

        if (find != null) {
            event.isCancelled = false
            this.npcPlugin.logger.warning("The EntitySpawnEvent was canceled by another plugin; however, the cancellation did not apply to the owner's holograms.")
        }
    }
}