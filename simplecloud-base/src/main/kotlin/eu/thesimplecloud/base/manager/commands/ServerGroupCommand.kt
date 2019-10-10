package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.base.manager.setup.ServerGroupSetup
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.startup.Launcher
import java.util.concurrent.TimeUnit

@Command("servergroup", true)
class ServerGroupCommand : ICommandHandler {

    @CommandSubPath("create")
    fun create(){
        Launcher.instance.setupManager.queueSetup(ServerGroupSetup())
    }

}