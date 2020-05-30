package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.extension.sendMessage

@Command("info", CommandType.CONSOLE, "cloud.command.info")
class InfoCommand : ICommandHandler {

    @CommandSubPath("wrapper <name>", "Prints some information about the specified wrapper")
    fun wrapper(commandSender: ICommandSender, @CommandArgument("name") name: String) {
        val wrapper = CloudAPI.instance.getWrapperManager().getWrapperByName(name)
        if (wrapper == null) {
            commandSender.sendMessage("manager.command.info.wrapper.not-exist", "The specified wrapper does not exist.")
            return
        }
        commandSender.sendMessage(wrapper.toString())
    }


    @CommandSubPath("service <name>", "Prints some information about the specified service")
    fun service(commandSender: ICommandSender, @CommandArgument("name") name: String) {
        val service = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(name)
        if (service == null) {
            commandSender.sendMessage("manager.command.info.service.not-exist", "The specified service does not exist.")
            return
        }
        commandSender.sendMessage(service.toString())
    }

    @CommandSubPath("group <name>", "Prints some information about the specified group")
    fun group(commandSender: ICommandSender, @CommandArgument("name") name: String) {
        val group = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(name)
        if (group == null) {
            commandSender.sendMessage("manager.command.info.group.not-exist", "The specified group does not exist.")
            return
        }
        commandSender.sendMessage(group.toString())
    }

    @CommandSubPath("player <name>", "Prints some information about the specified player")
    fun player(commandSender: ICommandSender, @CommandArgument("name") name: String) {
        val player = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(name)
        if (player == null) {
            commandSender.sendMessage("manager.command.info.player.not-exist", "The specified player does not exist.")
            return
        }
        commandSender.sendMessage(player.toString())
    }


}