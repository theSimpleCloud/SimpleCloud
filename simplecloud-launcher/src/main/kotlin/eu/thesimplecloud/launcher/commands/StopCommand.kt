package eu.thesimplecloud.launcher.commands

import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.09.2019
 * Time: 14:15+
 */
@Command("stop", CommandType.CONSOLE)
class StopCommand(): ICommandHandler {

    @CommandSubPath(description = "Stops the cloud")
    fun handleCommand(sender: ICommandSender) {
        sender.sendMessage("Stopping the cloud...")
        Launcher.instance.shutdown()
    }

}