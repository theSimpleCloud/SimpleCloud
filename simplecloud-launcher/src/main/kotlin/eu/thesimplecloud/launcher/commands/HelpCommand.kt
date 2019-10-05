package eu.thesimplecloud.launcher.commands

import eu.thesimplecloud.launcher.Launcher
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.09.2019
 * Time: 16:07
 */
@Command("help", false)
class HelpCommand(application: ICloudApplication) : ICommandHandler {

    @CommandSubPath
    fun handleCommand(sender: ICommandSender) {
        sender.sendMessage("commands.help.header", "Help | See all commands (%COMMAND_SIZE%", (Launcher.instance.commandManager.commands.map { it.path.split(" ")[0] }.toSet().size - 1).toString(), ")")
        Launcher.instance.commandManager.commands.forEach {
            if (!it.path.trim().split(" ")[0].equals("help", true)) {
                sender.sendMessage("commands.help.command", ">> %COMMAND_NAME%", it.path.trim(), " (%COMMAND_DESCRIPTION%", it.commandDescription, ")")
            }
        }
    }

}

/*
* {
* "launger-allcommands-command": ">> a (aaaa)",
* }
*
*
* */