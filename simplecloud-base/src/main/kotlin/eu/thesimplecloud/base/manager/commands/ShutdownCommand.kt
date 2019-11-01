package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.lib.service.ICloudService

@Command("shutdown", false)
class ShutdownCommand : ICommandHandler {

    @CommandSubPath("<name>", "Stops a service")
    fun shutdown(commandSender: ICommandSender, @CommandArgument("name") service: ICloudService) {
        service.shutdown()
        commandSender.sendMessage("manager.command.shutdown.success", "Stopping service.")
    }

}