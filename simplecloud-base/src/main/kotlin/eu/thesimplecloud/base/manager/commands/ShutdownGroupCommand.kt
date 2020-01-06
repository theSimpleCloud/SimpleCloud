package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath

@Command("shutdowngroup", false)
class ShutdownGroupCommand : ICommandHandler {

    @CommandSubPath("<group>", "Stops all services of a group.")
    fun startService(commandSender: ICommandSender, @CommandArgument("group") cloudServiceGroup: ICloudServiceGroup) {
        cloudServiceGroup.stopAllRunningServices()
        commandSender.sendMessage("manager.command.stop.success", "Stopping all services of group %GROUP%", cloudServiceGroup.getName())
    }

}