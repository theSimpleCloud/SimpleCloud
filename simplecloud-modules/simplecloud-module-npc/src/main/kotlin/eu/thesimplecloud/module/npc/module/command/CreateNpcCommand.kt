package eu.thesimplecloud.module.npc.module.command

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.module.npc.lib.config.npc.CloudNPCData
import eu.thesimplecloud.module.npc.lib.config.npc.LocationData
import eu.thesimplecloud.module.npc.lib.config.npc.NPCItem
import eu.thesimplecloud.module.npc.lib.config.npc.SkinData
import eu.thesimplecloud.module.npc.lib.config.npc.action.NPCAction
import eu.thesimplecloud.module.npc.lib.config.npc.settings.MobNPCSettings
import eu.thesimplecloud.module.npc.lib.config.npc.settings.NPCSettings
import eu.thesimplecloud.module.npc.lib.config.npc.settings.PlayerNPCSettings
import eu.thesimplecloud.module.npc.module.NPCModule
import eu.thesimplecloud.module.npc.module.command.provider.CloudNPCMobTypeCommandSuggestionProvider
import eu.thesimplecloud.module.npc.module.command.provider.ServicesWithoutProxiesCommandSuggestionProvider

/**
 * Created by MrManHD
 * Class create at 22.06.2023 23:19
 */

@Command("cloudnpc", CommandType.INGAME, "cloud.module.npc", ["npc", "npcs", "cloudnpcs"])
class CreateNpcCommand : ICommandHandler {

    private val npcModuleConfigHandler = NPCModule.instance.npcModuleConfigHandler

    @CommandSubPath("create player <id> <targetService> <displayName>", "Create a npc.")
    fun executeCreatePlayer(
        sender: ICommandSender,
        @CommandArgument("id") id: String,
        @CommandArgument("targetService", ServicesWithoutProxiesCommandSuggestionProvider::class) targetService: String,
        @CommandArgument("displayName") displayName: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModuleConfigHandler.load()

        if (config.npcsConfig.existNpcWithId(id)) {
            player.sendProperty("manager.command.npc.create.failed.id.already.exist")
            return
        }

        if (cantBeCreatedOnThisService(targetService)) {
            player.sendProperty("manager.command.npc.failed.group.not.found")
            return
        }

        val skinData = SkinData(
            "ewogICJ0aW1lc3RhbXAiIDogMTYyNDUyNjI0NjM2MywKICAicHJvZmlsZUlkIiA6ICIwNjlhNzlmNDQ0ZTk0NzI2YTViZWZjYTkwZTM4YWFmNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb3RjaCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yOTIwMDlhNDkyNWI1OGYwMmM3N2RhZGMzZWNlZjA3ZWE0Yzc0NzJmNjRlMGZkYzMyY2U1NTIyNDg5MzYyNjgwIgogICAgfQogIH0KfQ==",
            "K76X+5wYgbcKhUxr5ZJuF4MXquYNPM5ypUf6DdNz2k0+XaJlobLVVdETe2LotlHyj6ABoU3//8mGZnfwhdj2BiulOErpB6cQR4pMmIrW6T3TLCt4L8d9juQy7xy7Dw9sQngXWm2h3Cazm+205qa0apnvA/i+IGv+WeutP52kfGhJBAN7uBUQaut0NWBfFPL8Jo7DhwBvWf/KWVpcT9UcVQuS/dVP/VE0rrTTSf3x2/jGI0ksBEdOz5lROARCHwOA1sRDvP1nQHhZD1Uekj4Bmo6rsAjJCrzr++nK2IcaPMv1uTLv0sbsGe4JF884rqWHYzs7/Cc5lGv8FNy+QjHmTcISfjnlxwJIkI48KOmAjuaova+tU1gBHRFHqJR186Vw8gtIGHusitFr6rUuutODaHyJ1C9VnItyk5RF3eznsh+uUHSkT9NOCTAhx11UhaFjlIHgqHG3rRVmeFWyEKHE8Pk2yEAlROGPedp+oYEwMFbM97Q+og7W/RtSH+kYl9vNwpLrQEG2F0bQUtulwQrWzk8T2fKgPHncZIDS2YvQjrrHjjlG0bLbiakHGvRrMrLbrVtmQrKjOjLuc5j4M/quMoZpFz98q4uftCmNOyN9ZmoEjgFv5fOdsJDGJawSaug9VEieCWhuuPnXPx19GpT1TRzGRjDW9DqO08kNeCcRxq0="
        )

        createNewNpc(player, displayName, id, targetService, skinData)
    }

    @CommandSubPath("create mob <type> <id> <targetService> <displayName>", "Create a npc.")
    fun executeCreateMob(
        sender: ICommandSender,
        @CommandArgument("type", CloudNPCMobTypeCommandSuggestionProvider::class) type: String,
        @CommandArgument("id") id: String,
        @CommandArgument("targetService", ServicesWithoutProxiesCommandSuggestionProvider::class) targetService: String,
        @CommandArgument("displayName") displayName: String
    ) {
        val player = sender as ICloudPlayer
        val config = this.npcModuleConfigHandler.load()

        if (config.npcsConfig.existNpcWithId(id)) {
            player.sendProperty("manager.command.npc.create.failed.id.already.exist")
            return
        }

        if (cantBeCreatedOnThisService(targetService)) {
            player.sendProperty("manager.command.npc.failed.group.not.found")
            return
        }

        if (NPCModule.instance.getMobCollection()?.types?.contains(type.uppercase()) != true) {
            player.sendProperty("manager.command.npc.could.not.find.type")
            return
        }

        createNewNpc(player, displayName, id, targetService, mobType = type.uppercase())
    }

    private fun cantBeCreatedOnThisService(name: String): Boolean {
        val cloudServiceGroupManager = CloudAPI.instance.getCloudServiceGroupManager()
        val cloudServiceManager = CloudAPI.instance.getCloudServiceManager()
        return cloudServiceGroupManager.getServiceGroupByName(name) == null
                && cloudServiceManager.getCloudServiceByName(name) == null
    }

    private fun createNewNpc(
        player: ICloudPlayer,
        displayName: String,
        id: String,
        targetServiceGroup: String,
        skinData: SkinData = SkinData("", ""),
        mobType: String = ""
    ) {
        val config = this.npcModuleConfigHandler.load()
        player.getLocation().addResultListener {
            val npcSettings = NPCSettings(
                mobNPCSettings = MobNPCSettings(mobType, ""),
                playerNPCData = PlayerNPCSettings(skinData)
            )

            val cloudNPCData = CloudNPCData(
                displayName,
                id,
                mobType != "",
                player.getConnectedServerName(),
                targetServiceGroup,
                LocationData(it.groupName, it.worldName, it.x, it.y, it.z, it.yaw, it.pitch),
                NPCAction(),
                NPCItem(null, null),
                npcSettings,
                listOf("§8» §7Online §b%PLAYERS_ONLINE% §8«", "§8» §7%DISPLAYNAME% §8«")
            )

            config.npcsConfig.npcs.add(cloudNPCData)
            config.update()
            this.npcModuleConfigHandler.save(config)
            player.sendProperty("manager.command.npc.create.successfully")
        }.addFailureListener {
            player.sendProperty("manager.command.npc.create.failed.unknown.location")
        }
    }

}