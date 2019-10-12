package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.packets.template.PacketIOAddTemplate
import eu.thesimplecloud.lib.packets.template.PacketIODeleteTemplate
import eu.thesimplecloud.lib.template.impl.DefaultTemplate

@Command("template", true)
class TemplateCommand() : ICommandHandler {

    val templateManager = CloudLib.instance.getTemplateManager()

    @CommandSubPath("create <name>", "Creates a template")
    fun createTemplate(@CommandArgument("name") name: String) {
        if (name.length > 16) {
            Launcher.instance.consoleSender.sendMessage("manager.command.template.create.name-too-long", "The specified name must be shorter than 17 characters.")
        }
        if (templateManager.getTemplate(name) != null) {
            Launcher.instance.consoleSender.sendMessage("manager.command.template.create.already-exist", "Template %NAME%", name, " does already exist.")
            return
        }
        val template = DefaultTemplate(name)
        templateManager.addTemplate(template)
        Manager.instance.nettyServer.getClientManager().sendPacketToAllClients(PacketIOAddTemplate(template))
        Launcher.instance.consoleSender.sendMessage("manager.command.template.create.success", "Template %NAME%", name, " was registered")

        //---create directories
        template.getEveryDirectory().mkdirs()
    }

    @CommandSubPath("delete <name>", "Deletes a template")
    fun deleteTemplate(@CommandArgument("name") name: String) {
        if (templateManager.getTemplate(name) == null) {
            Launcher.instance.consoleSender.sendMessage("manager.command.template.delete.not-exist", "Template %NAME%", name, " does not exist.")
            return
        }
        templateManager.removeTemplate(name)
        Manager.instance.nettyServer.getClientManager().sendPacketToAllClients(PacketIODeleteTemplate(name))
        Launcher.instance.consoleSender.sendMessage("manager.command.template.delete.success", "Template %NAME%", name, " was deleted.")
    }

}