package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.console.command.provider.ServiceGroupCommandSuggestionProvider
import eu.thesimplecloud.launcher.extension.sendMessage

@Command("shutdowngroup", CommandType.CONSOLE_AND_INGAME, "simplecloud.command.shutdowngroup")
class ShutdownGroupCommand : ICommandHandler {

    @CommandSubPath("<group>", "Stops all services of a group.")
    fun startService(commandSender: ICommandSender, @CommandArgument("group", ServiceGroupCommandSuggestionProvider::class) cloudServiceGroup: ICloudServiceGroup) {
        if (cloudServiceGroup.getAllServices().isEmpty()){
            commandSender.sendMessage("manager.command.shutdowngroup.failure", "There are no running services of group %GROUP%", cloudServiceGroup.getName())
            return
        }
        cloudServiceGroup.stopAllServices()
        commandSender.sendMessage("manager.command.shutdowngroup.success", "Stopping all services of group %GROUP%", cloudServiceGroup.getName())
    }

}