package eu.thesimplecloud.module.npc.module.command

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.console.command.provider.ServiceGroupCommandSuggestionProvider
import eu.thesimplecloud.module.npc.lib.config.npc.*
import eu.thesimplecloud.module.npc.lib.config.npc.action.Action
import eu.thesimplecloud.module.npc.lib.config.npc.action.NPCAction
import eu.thesimplecloud.module.npc.lib.config.npc.settings.MobNPCSettings
import eu.thesimplecloud.module.npc.lib.config.npc.settings.NPCSettings
import eu.thesimplecloud.module.npc.lib.config.npc.settings.PlayerNPCSettings
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

    @CommandSubPath("create player <id> <targetGroup> <displayName>", "Create a npc.")
    fun handleCreate(
        sender: ICommandSender,
        @CommandArgument("id") id: String,
        @CommandArgument("targetGroup", ServiceGroupWithoutProxiesCommandSuggestionProvider::class) targetGroup: String,
        @CommandArgument("displayName") displayName: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.any { it.id == id }) {
            player.sendProperty("manager.command.npc.create.failed.id.already.exist")
            return
        }

        if (CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(targetGroup) == null) {
            player.sendProperty("manager.command.npc.failed.group.not.found")
            return
        }

        player.getLocation().addResultListener {
            val npcSettings = NPCSettings(
                mobNPCSettings = MobNPCSettings(""),
                playerNPCData = PlayerNPCSettings(
                    SkinData("ewogICJ0aW1lc3RhbXAiIDogMTYyNDUyNjI0NjM2MywKICAicHJvZmlsZUlkIiA6ICIwNjlhNzlmNDQ0ZTk0NzI2YTViZWZjYTkwZTM4YWFmNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb3RjaCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yOTIwMDlhNDkyNWI1OGYwMmM3N2RhZGMzZWNlZjA3ZWE0Yzc0NzJmNjRlMGZkYzMyY2U1NTIyNDg5MzYyNjgwIgogICAgfQogIH0KfQ==", "K76X+5wYgbcKhUxr5ZJuF4MXquYNPM5ypUf6DdNz2k0+XaJlobLVVdETe2LotlHyj6ABoU3//8mGZnfwhdj2BiulOErpB6cQR4pMmIrW6T3TLCt4L8d9juQy7xy7Dw9sQngXWm2h3Cazm+205qa0apnvA/i+IGv+WeutP52kfGhJBAN7uBUQaut0NWBfFPL8Jo7DhwBvWf/KWVpcT9UcVQuS/dVP/VE0rrTTSf3x2/jGI0ksBEdOz5lROARCHwOA1sRDvP1nQHhZD1Uekj4Bmo6rsAjJCrzr++nK2IcaPMv1uTLv0sbsGe4JF884rqWHYzs7/Cc5lGv8FNy+QjHmTcISfjnlxwJIkI48KOmAjuaova+tU1gBHRFHqJR186Vw8gtIGHusitFr6rUuutODaHyJ1C9VnItyk5RF3eznsh+uUHSkT9NOCTAhx11UhaFjlIHgqHG3rRVmeFWyEKHE8Pk2yEAlROGPedp+oYEwMFbM97Q+og7W/RtSH+kYl9vNwpLrQEG2F0bQUtulwQrWzk8T2fKgPHncZIDS2YvQjrrHjjlG0bLbiakHGvRrMrLbrVtmQrKjOjLuc5j4M/quMoZpFz98q4uftCmNOyN9ZmoEjgFv5fOdsJDGJawSaug9VEieCWhuuPnXPx19GpT1TRzGRjDW9DqO08kNeCcRxq0=")
                )
            )

            val cloudNPCData = CloudNPCData(displayName, id, false, targetGroup,
                LocationData(it.groupName, it.worldName, it.x, it.y,it.z, it.yaw, it.pitch),
                NPCAction(),
                NPCItem(null, null),
                npcSettings,
                listOf("§8» §7Online §b%PLAYERS_ONLINE% §8«", "§8» §7%DISPLAYNAME% §8«")
            )

            config.npcsConfig.npcs.add(cloudNPCData)
            config.update()
            this.npcModule.npcModuleConfigHandler.save(config)
            player.sendProperty("manager.command.npc.create.successfully")
        }.addFailureListener {
            player.sendProperty("manager.command.npc.create.failed.unknown.location")
        }
    }

    @CommandSubPath("create mob <type> <id> <targetGroup> <displayName>", "Create a npc.")
    fun handleCreate(
        sender: ICommandSender,
        @CommandArgument("type", CloudNPCMobTypeCommandSuggestionProvider::class) type: String,
        @CommandArgument("id") id: String,
        @CommandArgument("targetGroup", ServiceGroupWithoutProxiesCommandSuggestionProvider::class) targetGroup: String,
        @CommandArgument("displayName") displayName: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.any { it.id == id }) {
            player.sendProperty("manager.command.npc.create.failed.id.already.exist")
            return
        }

        if (CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(targetGroup) == null) {
            player.sendProperty("manager.command.npc.failed.group.not.found")
            return
        }

        if (this.npcModule.getMobCollection()?.types?.contains(type.uppercase()) != true) {
            player.sendProperty("manager.command.npc.could.not.find.type")
            return
        }

        player.getLocation().addResultListener {
            val npcSettings = NPCSettings(
                mobNPCSettings = MobNPCSettings(type.uppercase()),
                playerNPCData = PlayerNPCSettings(
                    SkinData("", ""),
                )
            )

            val cloudNPCData = CloudNPCData(displayName, id, true, targetGroup,
                LocationData(it.groupName, it.worldName, it.x, it.y,it.z, it.yaw, it.pitch),
                NPCAction(),
                NPCItem(null, null),
                npcSettings,
                listOf("§8» §7Online §b%PLAYERS_ONLINE% §8«", "§8» §7%DISPLAYNAME% §8«")
            )

            config.npcsConfig.npcs.add(cloudNPCData)
            config.update()
            this.npcModule.npcModuleConfigHandler.save(config)
            player.sendProperty("manager.command.npc.create.successfully")
        }.addFailureListener {
            player.sendProperty("manager.command.npc.create.failed.unknown.location")
        }
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

    @CommandSubPath("edit general <id> setTargetGroup <targetGroup>", "Edit the target group of npc.")
    fun handleEditTargetGroup(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String,
        @CommandArgument("targetGroup", ServiceGroupWithoutProxiesCommandSuggestionProvider::class) targetGroup: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.none { it.id.lowercase() == id.lowercase() }) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        if (CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(targetGroup) == null) {
            player.sendProperty("manager.command.npc.failed.group.not.found")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
        cloudNPCData.targetGroup = targetGroup
        config.update()
        this.npcModule.npcModuleConfigHandler.save(config)
        player.sendProperty("manager.command.npc.edit.target.group.successfully")
    }

    @CommandSubPath("edit general <id> setDisplayname <displayname>", "Edit the display name of npc.")
    fun handleEditDisplayName(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String,
        @CommandArgument("displayname") displayname: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.none { it.id.lowercase() == id.lowercase() }) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
        cloudNPCData.displayName = displayname
        config.update()
        this.npcModule.npcModuleConfigHandler.save(config)
        player.sendProperty("manager.command.npc.edit.display.name.successfully")
    }

    @CommandSubPath("edit general <id> setLocation", "Edit the location name of npc.")
    fun handleEditLocation(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.none { it.id.lowercase() == id.lowercase() }) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        player.getLocation().addResultListener { location ->
            val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
            cloudNPCData.locationData = LocationData(location.groupName, location.worldName, location.x, location.y,location.z, location.yaw, location.pitch)
            config.update()
            this.npcModule.npcModuleConfigHandler.save(config)
            player.sendProperty("manager.command.npc.edit.location.successfully")
        }.addFailureListener {
            player.sendProperty("manager.command.npc.edit.location.failed")
        }
    }

    @CommandSubPath("edit general <id> toggle glowing", "Edit the mode of glowing.")
    fun handleEditToggleGlow(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.none { it.id.lowercase() == id.lowercase() }) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
        cloudNPCData.npcSettings.glowing = !cloudNPCData.npcSettings.glowing

        config.update()
        this.npcModule.npcModuleConfigHandler.save(config)

        player.sendProperty("manager.command.npc.edit.toggle.glowing.successfully")
    }

    @CommandSubPath("edit general <id> toggle fire", "Edit the mode of fire.")
    fun handleEditToggleFire(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.none { it.id.lowercase() == id.lowercase() }) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }
        cloudNPCData.npcSettings.onFire = !cloudNPCData.npcSettings.onFire

        config.update()
        this.npcModule.npcModuleConfigHandler.save(config)

        player.sendProperty("manager.command.npc.edit.toggle.fire.successfully")
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

    @CommandSubPath("edit general <id> setAction leftClick <action>", "Edit the action of mob.")
    fun handleEditActionLeftClick(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String,
        @CommandArgument("action", CloudNPCActionCommandSuggestionProvider::class) action: String
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

        if (!Action.values().map { it.name }.contains(action.uppercase())) {
            player.sendProperty("manager.command.npc.could.not.find.action")
            return
        }
        cloudNPCData.npcAction.leftClick = Action.valueOf(action)

        config.update()
        this.npcModule.npcModuleConfigHandler.save(config)
        player.sendProperty("manager.command.npc.edit.action.left.click.successfully")
    }

    @CommandSubPath("edit general <id> setAction rightClick <action>", "Edit the action of mob.")
    fun handleEditActionRightClick(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String,
        @CommandArgument("action", CloudNPCActionCommandSuggestionProvider::class) action: String
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

        if (!Action.values().map { it.name }.contains(action.uppercase())) {
            player.sendProperty("manager.command.npc.could.not.find.action")
            return
        }
        cloudNPCData.npcAction.rightClick = Action.valueOf(action)

        config.update()
        this.npcModule.npcModuleConfigHandler.save(config)
        player.sendProperty("manager.command.npc.edit.action.right.click.successfully")
    }

    @CommandSubPath("edit general <id> setItem left <material>", "Edit the item on the left hand.")
    fun handleEditItemLeft(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String,
        @CommandArgument("material", CloudNPCItemListCommandSuggestionProvider::class) type: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.none { it.id.lowercase() == id.lowercase() }) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }

        if (this.npcModule.getMaterialCollection()?.types?.contains(type.uppercase()) != true) {
            player.sendProperty("manager.command.npc.could.not.find.material")
            return
        }
        cloudNPCData.npcItem.leftHand = type.uppercase()

        config.update()
        this.npcModule.npcModuleConfigHandler.save(config)
        player.sendProperty("manager.command.npc.edit.item.left.successfully")
    }

    @CommandSubPath("edit general <id> setItem right <material>", "Edit the item on the right hand.")
    fun handleEditItemRight(
        sender: ICommandSender,
        @CommandArgument("id", CloudNPCIDCommandSuggestionProvider::class) id: String,
        @CommandArgument("material", CloudNPCItemListCommandSuggestionProvider::class) type: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModule.npcModuleConfigHandler.load()

        if (config.npcsConfig.npcs.none { it.id.lowercase() == id.lowercase() }) {
            player.sendProperty("manager.command.npc.id.not.found.")
            return
        }

        val cloudNPCData = config.npcsConfig.npcs.first { it.id.lowercase() == id.lowercase() }

        if (this.npcModule.getMaterialCollection()?.types?.contains(type.uppercase()) != true) {
            player.sendProperty("manager.command.npc.could.not.find.material")
            return
        }
        cloudNPCData.npcItem.rightHand = type.uppercase()

        config.update()
        this.npcModule.npcModuleConfigHandler.save(config)
        player.sendProperty("manager.command.npc.edit.item.right.successfully")
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