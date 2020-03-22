package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.base.manager.setup.WrapperSetup
import eu.thesimplecloud.base.manager.setup.groups.LobbyGroupSetup
import eu.thesimplecloud.base.manager.setup.groups.ProxyGroupSetup
import eu.thesimplecloud.base.manager.setup.groups.ServerGroupSetup
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.extension.sendPacketToAllAuthenticatedClients
import eu.thesimplecloud.api.network.packets.template.PacketIOUpdateTemplate
import eu.thesimplecloud.api.template.impl.DefaultTemplate
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.extension.sendMessage

@Command("create", CommandType.CONSOLE)
class CreateCommand : ICommandHandler {

    val templateManager = CloudAPI.instance.getTemplateManager()


    @CommandSubPath("lobbygroup", "Creates a lobby group")
    fun createLobbyGroup(){
        Launcher.instance.setupManager.queueSetup(LobbyGroupSetup())
    }

    @CommandSubPath("proxygroup", "Creates a proxy group")
    fun createProxyGroup(){
        Launcher.instance.setupManager.queueSetup(ProxyGroupSetup())
    }

    @CommandSubPath("servergroup", "Creates a server group")
    fun createServerGroup(){
        Launcher.instance.setupManager.queueSetup(ServerGroupSetup())
    }

    @CommandSubPath("wrapper", "Creates a wrapper")
    fun createWrapper(){
        Launcher.instance.setupManager.queueSetup(WrapperSetup())
    }

    @CommandSubPath("template <name>", "Creates a template")
    fun createTemplate(@CommandArgument("name") name: String) {
        if (name.length > 16) {
            Launcher.instance.consoleSender.sendMessage("manager.command.create.template.name-too-long", "The specified name must be shorter than 17 characters.")
        }
        if (templateManager.getTemplateByName(name) != null) {
            Launcher.instance.consoleSender.sendMessage("manager.command.create.template.already-exist", "Template %NAME%", name, " does already exist.")
            return
        }
        val template = DefaultTemplate(name)
        templateManager.updateTemplate(template)
        Manager.instance.communicationServer.getClientManager().sendPacketToAllAuthenticatedClients(PacketIOUpdateTemplate(template))
        Launcher.instance.consoleSender.sendMessage("manager.command.create.template.success", "Template %NAME%", name, " created")

        //---create directories
        template.getDirectory().mkdirs()
    }



}