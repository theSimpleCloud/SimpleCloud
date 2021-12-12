package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.startup.Launcher

@Command("clear", CommandType.CONSOLE, "cloud.command.clear")
class ClearCommand : ICommandHandler {

    @CommandSubPath("", "Clears the Console")
    fun handleClear(commandSender: ICommandSender) {
        Launcher.instance.clearConsole()
    }
}