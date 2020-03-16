package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.extension.sendMessage

@Command("start", CommandType.CONSOLE_AND_INGAME)
class StartCommand : ICommandHandler {

    @CommandSubPath("<group>", "Starts a service.")
    fun startService(commandSender: ICommandSender, @CommandArgument("group") cloudServiceGroup: ICloudServiceGroup) {
        cloudServiceGroup.startNewService()
        commandSender.sendMessage("manager.command.start.success", "Trying to start a new service of group %GROUP%", cloudServiceGroup.getName())
    }

}