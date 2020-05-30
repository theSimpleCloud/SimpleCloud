package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.extension.sendMessage

@Command("execute", CommandType.CONSOLE_AND_INGAME, "cloud.command.execute", ["exec"])
class ExecuteCommand : ICommandHandler {

    @CommandSubPath("", "Execute a command on the specified service")
    fun handle(commandSender: ICommandSender, args: Array<String>) {
        if (args.size < 2) {
            val prefix = if (commandSender is ICloudPlayer) "cloud " else ""
            commandSender.sendMessage("manager.command.execute.usage", "&cUsage: ${prefix}execute <service> <command>")
            return
        }
        val serviceName = args[0]
        val service = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(serviceName)
        if (service == null) {
            commandSender.sendMessage("manager.command.execute.service-not-found", "&cService not found.")
            return
        }
        service.executeCommand(args.drop(1).joinToString(" "))
        commandSender.sendMessage("manager.command.execute.success", "&aExecuted command.")
    }

}