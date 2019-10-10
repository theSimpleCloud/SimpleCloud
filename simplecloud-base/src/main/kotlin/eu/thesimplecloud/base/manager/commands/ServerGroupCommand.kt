package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import java.util.concurrent.TimeUnit

@Command("servergroup", true)
class ServerGroupCommand : ICommandHandler {

    @CommandSubPath("create")
    fun create(){

    }

}