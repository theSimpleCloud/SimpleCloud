package eu.thesimplecloud.launcher.commands

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.setups.JavaSetup
import eu.thesimplecloud.launcher.startup.Launcher

@Command("create", CommandType.CONSOLE)
class CreateJavaVersion : ICommandHandler {

    @CommandSubPath("javaversion", "Creates a new java version")
    fun createJavaVersion(sender: ICommandSender) {
        Launcher.instance.setupManager.queueSetup(JavaSetup())
    }
}