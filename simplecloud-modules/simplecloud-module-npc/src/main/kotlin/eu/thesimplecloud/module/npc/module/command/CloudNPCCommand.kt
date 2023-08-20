package eu.thesimplecloud.module.npc.module.command

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.module.npc.lib.config.npc.*
import eu.thesimplecloud.module.npc.lib.config.npc.action.Action
import eu.thesimplecloud.module.npc.module.NPCModule
import eu.thesimplecloud.module.npc.module.command.provider.*

@Command("cloudnpc", CommandType.INGAME, "cloud.module.npc", ["npc", "npcs", "cloudnpcs"])
class CloudNPCCommand(
    private val npcModule: NPCModule
): ICommandHandler {

    @CommandSubPath("reload", "Reloads the npc module")
    fun handleReload(sender: ICommandSender) {
        val config = this.npcModule.npcModuleConfigHandler.load()
        config.update()
        sender.sendProperty("manager.command.npc.reload")
    }

    @CommandSubPath("delete <id>", "Edit the target group of npc.")
    fun handleDelete(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.none { it.id.lowercase() == id.lowercase() }) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        config.npcsConfig.npcs.removeIf {  it.id.lowercase() == id.lowercase()  }

        config.update()
        this.npcModule.npcModuleConfigHandler.save(config)
        player.sendProperty("manager.command.npc.delete.successfully")
    }


    @CommandSubPath("edit player <id> toggle lookAtPlayer", "Edit the mode of lookAtPlayer.")
    fun handleEditToggleLookAtPlayer(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDPlayerCommandSuggestionProvider::class) id: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.none { it.id.lowercase() == id.lowercase() }) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
        cloudNPCData.npcSettings.playerNPCData.lookAtPlayer = !cloudNPCData.npcSettings.playerNPCData.lookAtPlayer

        config.update()
        this.npcModule.npcModuleConfigHandler.save(config)

        player.sendProperty("manager.command.npc.edit.toggle.lookAtPlayer.successfully")
    }

    @CommandSubPath("edit player <id> toggle hitWhenPlayerHits", "Edit the mode of hitWhenPlayerHits.")
    fun handleEditToggleHitWhenPlayerHits(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDPlayerCommandSuggestionProvider::class) id: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.none { it.id.lowercase() == id.lowercase() }) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
        cloudNPCData.npcSettings.playerNPCData.hitWhenPlayerHits = !cloudNPCData.npcSettings.playerNPCData.hitWhenPlayerHits

        config.update()
        this.npcModule.npcModuleConfigHandler.save(config)

        player.sendProperty("manager.command.npc.edit.toggle.hitWhenPlayerHits.successfully")
    }

    @CommandSubPath("edit player <id> toggle sneakWhenPlayerSneaks", "Edit the mode of sneakWhenPlayerSneaks.")
    fun handleEditToggleSneakWhenPlayerSneaks(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDPlayerCommandSuggestionProvider::class) id: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.none { it.id.lowercase() == id.lowercase() }) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
        cloudNPCData.npcSettings.playerNPCData.sneakWhenPlayerSneaks = !cloudNPCData.npcSettings.playerNPCData.sneakWhenPlayerSneaks

        config.update()
        this.npcModule.npcModuleConfigHandler.save(config)

        player.sendProperty("manager.command.npc.edit.toggle.sneakWhenPlayerSneaks.successfully")
    }

    @CommandSubPath("edit player <id> toggle flyingWithElytra", "Edit the mode of flyingWithElytra.")
    fun handleEditToggleSneakFlyingWithElytra(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDPlayerCommandSuggestionProvider::class) id: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.none { it.id.lowercase() == id.lowercase() }) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
        cloudNPCData.npcSettings.playerNPCData.flyingWithElytra = !cloudNPCData.npcSettings.playerNPCData.flyingWithElytra

        config.update()
        this.npcModule.npcModuleConfigHandler.save(config)

        player.sendProperty("manager.command.npc.edit.toggle.flyingWithElytra.successfully")
    }

    @CommandSubPath("edit player <id> setSkinByMineskinID <mineskinID>", "Edit the skin of npc.")
    fun handleEditSkinByMineskinID(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDPlayerCommandSuggestionProvider::class) id: String,
        @CommandArgument("mineskinID") mineskinID: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.none { it.id.lowercase() == id.lowercase() }) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        this.npcModule.skinHandler.getSkinConfigByID(mineskinID).thenAccept {
            if (it != null) {
                val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
                cloudNPCData.npcSettings.playerNPCData.skinData.value = it.value
                cloudNPCData.npcSettings.playerNPCData.skinData.signature = it.signature

                config.update()
                this.npcModule.npcModuleConfigHandler.save(config)

                player.sendProperty("manager.command.npc.edit.skin.mineskin.successfully")
            } else {
                player.sendProperty("manager.command.npc.edit.skin.mineskin.failed")
            }
        }
    }

    @CommandSubPath("edit player <id> setSkinByName <name>", "Edit the skin of npc.")
    fun handleEditSkinByName(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDPlayerCommandSuggestionProvider::class) id: String,
        @CommandArgument("name") name: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.none { it.id.lowercase() == id.lowercase() }) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        this.npcModule.skinHandler.getSkinConfigByName(name).thenAccept {
            if (it != null) {
                val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
                cloudNPCData.npcSettings.playerNPCData.skinData.value = it.value
                cloudNPCData.npcSettings.playerNPCData.skinData.signature = it.signature

                config.update()
                this.npcModule.npcModuleConfigHandler.save(config)

                player.sendProperty("manager.command.npc.edit.skin.mineskin.successfully")
            } else {
                player.sendProperty("manager.command.npc.edit.skin.mineskin.failed")
            }
        }
    }

    @CommandSubPath("edit mob <id> setMobType <type>", "Edit the type of mob.")
    fun handleEditMobType(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDMobCommandSuggestionProvider::class) id: String,
        @CommandArgument("type", CloudNPCMobTypeCommandSuggestionProvider::class) type: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.none { it.id.lowercase() == id.lowercase() }) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }

        if (!cloudNPCData.isMob) {
            player.sendProperty("manager.command.npc.is.not.mob")
            return
        }

        if (this.npcModule.getMobCollection()?.types?.contains(type.uppercase()) != true) {
            player.sendProperty("manager.command.npc.could.not.find.type")
            return
        }
        cloudNPCData.npcSettings.mobNPCSettings.mobType = type.uppercase()

        config.update()
        this.npcModule.npcModuleConfigHandler.save(config)
        player.sendProperty("manager.command.npc.edit.mob.type.successfully")
    }

    @CommandSubPath("list", "List of npcs.")
    fun handleList(sender: ICommandSender) {
        val config = this.npcModule.npcModuleConfigHandler.load()
        sender.sendProperty("manager.command.npc.list.header", config.npcsConfig.npcs.size.toString())
        config.npcsConfig.npcs.forEach {
            sender.sendProperty("manager.command.npc.list.entry", it.id, it.locationData.locationGroup ,it.targetGroup)
        }
    }
}