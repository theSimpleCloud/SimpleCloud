package eu.thesimplecloud.module.sign.manager.command

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.module.sign.lib.SignModuleConfig
import eu.thesimplecloud.module.sign.manager.SignsModule

@Command("signs", CommandType.CONSOLE_AND_INGAME, "simplecloud.command.signs")
class SignsCommand : ICommandHandler {

    @CommandSubPath("reload", "Reloads the config")
    fun handleReload(commandSender: ICommandSender) {
        SignsModule.INSTANCE.reloadConfig()
        commandSender.sendMessage("manager.command.signs.reload", "&aConfig reloaded.")
    }

    @CommandSubPath("layouts", "Lists all layouts")
    fun handleLayouts(commandSender: ICommandSender) {
        val names = SignModuleConfig.INSTANCE.obj.signLayouts.map { it.name }
                .filter { it != "SEARCHING" }
                .filter { it != "STARTING" }
                .filter { it != "MAINTENANCE" }

        commandSender.sendMessage("&eLayouts&8: &7${names.joinToString()}")
    }

    @CommandSubPath("group <group> layout <layout>", "Sets the layout for this group.")
    fun handleLayout(commandSender: ICommandSender, @CommandArgument("group") groupName: String, @CommandArgument("layout") layoutName: String) {
        val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(groupName)
        if (serviceGroup == null) {
            commandSender.sendMessage("manager.command.signs.group-not-found", "§cGroup not found.")
            return
        }
        val signModuleConfig = SignModuleConfig.INSTANCE.obj
        val signLayout = signModuleConfig.getSignLayoutByName(layoutName)
        if (signLayout == null) {
            commandSender.sendMessage("manager.command.signs.layout-not-found", "§cLayout not found.")
            return
        }
        signModuleConfig.groupToLayout.putGroupToLayout(serviceGroup.getName(), signLayout.name)
        signModuleConfig.update()
        commandSender.sendMessage("manager.command.signs.layout-set", "§7Group &e%GROUP%", serviceGroup.getName(), " &7is now using the layout &e%LAYOUT%", signLayout.name, "&7.")
    }

}