package eu.thesimplecloud.launcher.commands

import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.Launcher
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.09.2019
 * Time: 14:15
 */
@Command("stop", true)
class StopCommand(val application: ICloudApplication): ICommandHandler {

    @CommandSubPath(description = "Stops the cloud")
    fun handleCommand(sender: ICommandSender) {
        application.shutdown()
        sender.sendMessage("Stopping the cloud...")
    }

}