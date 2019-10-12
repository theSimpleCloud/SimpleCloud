package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.base.manager.setup.groups.ServerGroupSetup
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.startup.Launcher

@Command("servergroup", true)
class ServerGroupCommand() : ICommandHandler {

    @CommandSubPath("create", "Creates a server group")
    fun create(){
        Launcher.instance.setupManager.queueSetup(ServerGroupSetup())
    }

}