package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.base.manager.setup.groups.ProxyGroupSetup
import eu.thesimplecloud.base.manager.setup.groups.ServerGroupSetup
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.startup.Launcher

@Command("proxygroup", true)
class ProxyGroupCommand() : ICommandHandler {

    @CommandSubPath("create", "Creates a proxy group")
    fun create(){
        Launcher.instance.setupManager.queueSetup(ProxyGroupSetup())
    }

}