package eu.thesimplecloud.module.npc.module

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.sync.`object`.GlobalPropertyUpdatedEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.npc.lib.config.NPCModuleConfig
import eu.thesimplecloud.module.npc.lib.config.NPCModuleConfigHandler
import eu.thesimplecloud.module.npc.lib.type.MaterialType
import eu.thesimplecloud.module.npc.lib.type.MobType
import eu.thesimplecloud.module.npc.module.command.CloudNPCCommand
import eu.thesimplecloud.module.npc.module.skin.MineSkinHandler

class NPCModule: ICloudModule {

    val npcModuleConfigHandler = NPCModuleConfigHandler()
    val mineSkinHandler = MineSkinHandler()

    override fun onEnable() {
        instance = this

        Launcher.instance.commandManager.registerCommand(this, CloudNPCCommand(this))

        val npcListConfig = this.npcModuleConfigHandler.load()
        this.npcModuleConfigHandler.save(npcListConfig)

        CloudAPI.instance.getGlobalPropertyHolder().setProperty("npc-config", npcListConfig)

        CloudAPI.instance.getEventManager().registerListener(this, object : IListener {

            @CloudEventHandler
            fun handleUpdate(event: GlobalPropertyUpdatedEvent) {
                if (event.propertyName == "npc-config") {
                    val property = event.property as IProperty<NPCModuleConfig>
                    npcModuleConfigHandler.save(property.getValue())
                }
            }

        })
    }

    override fun onDisable() {

    }

    fun getMobCollection(): MobType? {
        val globalPropertyHolder = CloudAPI.instance.getGlobalPropertyHolder()
        if (globalPropertyHolder.hasProperty("npc-type-list")) {
            val property = globalPropertyHolder.getProperty<MobType>("npc-type-list")
            return property?.getValue()
        }
        return null
    }


    fun getMaterialCollection(): MaterialType? {
        val globalPropertyHolder = CloudAPI.instance.getGlobalPropertyHolder()
        if (globalPropertyHolder.hasProperty("npc-material-list")) {
            val property = globalPropertyHolder.getProperty<MaterialType>("npc-material-list")
            return property?.getValue()
        }
        return null
    }

    companion object {
        lateinit var instance: NPCModule
    }
}