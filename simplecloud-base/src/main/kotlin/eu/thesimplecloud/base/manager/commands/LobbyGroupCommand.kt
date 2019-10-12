package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.base.manager.setup.groups.LobbyGroupSetup
import eu.thesimplecloud.base.manager.setup.groups.ServerGroupSetup
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.startup.Launcher

@Command("lobbygroup", true)
class LobbyGroupCommand() : ICommandHandler {

    @CommandSubPath("create", "Creates a lobby group")
    fun create(){
        Launcher.instance.setupManager.queueSetup(LobbyGroupSetup())
    }

}