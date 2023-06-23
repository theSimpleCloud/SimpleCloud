package eu.thesimplecloud.module.npc.module.command

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.module.npc.lib.config.npc.LocationData
import eu.thesimplecloud.module.npc.lib.config.npc.action.Action
import eu.thesimplecloud.module.npc.module.NPCModule
import eu.thesimplecloud.module.npc.module.command.provider.CloudNPCActionCommandSuggestionProvider
import eu.thesimplecloud.module.npc.module.command.provider.CloudNPCIDCommandSuggestionProvider
import eu.thesimplecloud.module.npc.module.command.provider.CloudNPCItemListCommandSuggestionProvider
import eu.thesimplecloud.module.npc.module.command.provider.ServicesWithoutProxiesCommandSuggestionProvider

/**
 * Created by MrManHD
 * Class create at 23.06.2023 13:20
 */

@Command("cloudnpc", CommandType.INGAME, "cloud.module.npc", ["npc", "npcs", "cloudnpcs"])
class EditGeneralNpcCommand : ICommandHandler {

    private val npcModuleConfigHandler = NPCModule.instance.npcModuleConfigHandler

    @CommandSubPath("edit general <id> setDisplayname <displayname>", "Edit the display name of npc.")
    fun executeEditDisplayName(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String,
        @CommandArgument("displayname") displayname: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModuleConfigHandler.load()

        if (!config.npcsConfig.existNpcWithId(id)) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
        cloudNPCData.displayName = displayname
        config.update()
        this.npcModuleConfigHandler.save(config)
        player.sendProperty("manager.command.npc.edit.display.name.successfully")
    }

    @CommandSubPath("edit general <id> setLocation", "Edit the location name of npc.")
    fun executeEditLocation(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModuleConfigHandler.load()

        if (!config.npcsConfig.existNpcWithId(id)) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        player.getLocation().addResultListener { location ->
            val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
            cloudNPCData.locationData = LocationData(location.groupName, location.worldName, location.x, location.y,location.z, location.yaw, location.pitch)
            config.update()
            this.npcModuleConfigHandler.save(config)
            player.sendProperty("manager.command.npc.edit.location.successfully")
        }.addFailureListener {
            player.sendProperty("manager.command.npc.edit.location.failed")
        }
    }

    @CommandSubPath("edit general <id> toggle glowing", "Edit the mode of glowing.")
    fun executeEditGlowing(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModuleConfigHandler.load()

        if (!config.npcsConfig.existNpcWithId(id)) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
        cloudNPCData.npcSettings.glowing = !cloudNPCData.npcSettings.glowing

        config.update()
        this.npcModuleConfigHandler.save(config)

        player.sendProperty("manager.command.npc.edit.toggle.glowing.successfully")
    }

    @CommandSubPath("edit general <id> toggle fire", "Edit the mode of fire.")
    fun executeEditFire(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModuleConfigHandler.load()

        if (!config.npcsConfig.existNpcWithId(id)) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
        cloudNPCData.npcSettings.onFire = !cloudNPCData.npcSettings.onFire

        config.update()
        this.npcModuleConfigHandler.save(config)

        player.sendProperty("manager.command.npc.edit.toggle.fire.successfully")
    }

    @CommandSubPath("edit general <id> setAction leftClick <action>", "Edit the action of mob.")
    fun executeEditLeftAction(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String,
        @CommandArgument("action", CloudNPCActionCommandSuggestionProvider::class) action: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModuleConfigHandler.load()

        if (!config.npcsConfig.existNpcWithId(id)) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }

        if (!Action.values().map { it.name }.contains(action.uppercase())) {
            player.sendProperty("manager.command.npc.could.not.find.action")
            return
        }
        cloudNPCData.npcAction.leftClick = Action.valueOf(action)

        config.update()
        this.npcModuleConfigHandler.save(config)
        player.sendProperty("manager.command.npc.edit.action.left.click.successfully")
    }

    @CommandSubPath("edit general <id> setRunCommand <commandName>", "Edit the run command of mob.")
    fun executeEditRunCommand(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String,
        @CommandArgument("commandName") commandName: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModuleConfigHandler.load()

        if (!config.npcsConfig.existNpcWithId(id)) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
        cloudNPCData.npcSettings.mobNPCSettings.runCommandName = commandName

        config.update()
        this.npcModuleConfigHandler.save(config)
        player.sendProperty("manager.command.npc.edit.run-command.successfully")
    }

    @CommandSubPath("edit general <id> setAction rightClick <action>", "Edit the action of mob.")
    fun executeEditRightAction(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String,
        @CommandArgument("action", CloudNPCActionCommandSuggestionProvider::class) action: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModuleConfigHandler.load()

        if (!config.npcsConfig.existNpcWithId(id)) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }

        if (!Action.values().map { it.name }.contains(action.uppercase())) {
            player.sendProperty("manager.command.npc.could.not.find.action")
            return
        }
        cloudNPCData.npcAction.rightClick = Action.valueOf(action)

        config.update()
        this.npcModuleConfigHandler.save(config)
        player.sendProperty("manager.command.npc.edit.action.right.click.successfully")
    }

    @CommandSubPath("edit general <id> setItem left <material>", "Edit the item on the left hand.")
    fun executeEditLeftItem(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String,
        @CommandArgument("material", CloudNPCItemListCommandSuggestionProvider::class) type: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModuleConfigHandler.load()

        if (!config.npcsConfig.existNpcWithId(id)) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }

        if (NPCModule.instance.getMaterialCollection()?.types?.contains(type.uppercase()) != true) {
            player.sendProperty("manager.command.npc.could.not.find.material")
            return
        }
        cloudNPCData.npcItem.leftHand = type.uppercase()

        config.update()
        this.npcModuleConfigHandler.save(config)
        player.sendProperty("manager.command.npc.edit.item.left.successfully")
    }

    @CommandSubPath("edit general <id> setItem right <material>", "Edit the item on the right hand.")
    fun executeEditRightItem(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String,
        @CommandArgument("material", CloudNPCItemListCommandSuggestionProvider::class) type: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModuleConfigHandler.load()

        if (!config.npcsConfig.existNpcWithId(id)) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }

        if (NPCModule.instance.getMaterialCollection()?.types?.contains(type.uppercase()) != true) {
            player.sendProperty("manager.command.npc.could.not.find.material")
            return
        }
        cloudNPCData.npcItem.rightHand = type.uppercase()

        config.update()
        this.npcModuleConfigHandler.save(config)
        player.sendProperty("manager.command.npc.edit.item.right.successfully")
    }

    @CommandSubPath("edit general <id> setTargetGroup <targetService>", "Edit the target group of npc.")
    fun executeEditTargetGroup(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String,
        @CommandArgument("targetService", ServicesWithoutProxiesCommandSuggestionProvider::class) targetService: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModuleConfigHandler.load()

        if (!config.npcsConfig.existNpcWithId(id)) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        if (cantBeCreatedOnThisService(targetService)) {
            player.sendProperty("manager.command.npc.failed.group.not.found")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
        cloudNPCData.targetGroup = targetService
        config.update()
        this.npcModuleConfigHandler.save(config)
        player.sendProperty("manager.command.npc.edit.target.group.successfully")
    }

    private fun cantBeCreatedOnThisService(name: String): Boolean {
        val cloudServiceGroupManager = CloudAPI.instance.getCloudServiceGroupManager()
        val cloudServiceManager = CloudAPI.instance.getCloudServiceManager()
        return cloudServiceGroupManager.getServiceGroupByName(name) == null
                && cloudServiceManager.getCloudServiceByName(name) == null
    }
}