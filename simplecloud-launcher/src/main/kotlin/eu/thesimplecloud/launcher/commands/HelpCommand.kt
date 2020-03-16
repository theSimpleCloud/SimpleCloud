package eu.thesimplecloud.launcher.commands

import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.launcher.console.ConsoleSender
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.extension.sendMessage

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.09.2019
 * Time: 16:07
 */
@Command("help", CommandType.CONSOLE_AND_INGAME)
class HelpCommand() : ICommandHandler {

    @CommandSubPath
    fun handleCommand(sender: ICommandSender) {
        val allCommands = Launcher.instance.commandManager.commands
                .filter { it.commandType != CommandType.INGAME }
                .filter { !it.path.startsWith("help") }

        if (sender is ConsoleSender) {
            val commandAmount = Launcher.instance.commandManager.commands
                    .map { it.path.split(" ")[0] }.toSet().size - 1
            sender.sendMessage("commands.help.header", "Help | See all commands (%COMMAND_SIZE%", commandAmount.toString(), ")")

            allCommands.forEach {
                sender.sendMessage("commands.help.command", ">> %COMMAND_NAME%", it.path.trim(), " (%COMMAND_DESCRIPTION%", it.commandDescription, ")")
            }
        } else {
            sender as ICloudPlayer
            allCommands.filter { it.commandType != CommandType.CONSOLE }.forEach {
                sender.sendMessage("commands.help.command.ingame", "&8>> &7%COMMAND_NAME%", it.getPathWithCloudPrefixIfRequired().trim())
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