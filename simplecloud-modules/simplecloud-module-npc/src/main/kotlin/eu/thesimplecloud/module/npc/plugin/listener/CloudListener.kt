package eu.thesimplecloud.module.npc.plugin.listener

import eu.thesimplecloud.api.event.sync.`object`.GlobalPropertyUpdatedEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.module.npc.lib.config.NPCModuleConfig
import eu.thesimplecloud.module.npc.plugin.NPCPlugin
import org.bukkit.Bukkit

class CloudListener(
    private val npcPlugin: NPCPlugin
): IListener {

    @CloudEventHandler
    fun handleUpdate(event: GlobalPropertyUpdatedEvent) {
        if (event.propertyName == "npc-config") {
            val property = event.property as IProperty<NPCModuleConfig>
            Bukkit.getScheduler().runTask(this.npcPlugin, Runnable {
                updateNPCs(property.getValue())
            })
        }
    }

    private fun updateNPCs(config: NPCModuleConfig) {
        this.npcPlugin.serverNPCHandler!!.deleteNPCs()

        this.npcPlugin.serverNPCHandler!!.config = config

        this.npcPlugin.serverNPCHandler!!.createNPCs()
    }
}