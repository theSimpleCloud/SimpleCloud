package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup

@Command("start", false)
class StartCommand : ICommandHandler {

    @CommandSubPath("<group>", "Starts a service.")
    fun startService(commandSender: ICommandSender, @CommandArgument("group") cloudServiceGroup: ICloudServiceGroup) {
        cloudServiceGroup.startNewService()
        commandSender.sendMessage("manager.command.start.success", "Trying to start a new service of group %GROUP%", cloudServiceGroup.getName())
    }

}