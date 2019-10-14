package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.packets.template.PacketIODeleteTemplate

@Command("delete", true)
class DeleteCommand : ICommandHandler {

    val templateManager = CloudLib.instance.getTemplateManager()

    @CommandSubPath("template <name>", "Deletes a template")
    fun deleteTemplate(@CommandArgument("name") name: String) {
        if (templateManager.getTemplate(name) == null) {
            Launcher.instance.consoleSender.sendMessage("manager.command.delete.template.not-exist", "Template %NAME%", name, " does not exist.")
            return
        }
        if (CloudLib.instance.getCloudServiceGroupManager().getAllGroups().any { it.getTemplateName().equals(name, true) }) {
            Launcher.instance.consoleSender.sendMessage("manager.command.delete.template.in-use", "Template %NAME%", name, " is in use by registered service groups. Delete them first.")
            return
        }
        templateManager.removeTemplate(name)
        Manager.instance.communicationServer.getClientManager().sendPacketToAllClients(PacketIODeleteTemplate(name))
        Launcher.instance.consoleSender.sendMessage("manager.command.delete.template.success", "Template %NAME%", name, " was deleted.")
    }

    @CommandSubPath("group <name>", "Deletes a group")
    fun deleteGroup(@CommandArgument("name") name: String) {
        val serviceGroup = CloudLib.instance.getCloudServiceGroupManager().getServiceGroup(name)
        if (serviceGroup == null){
            Launcher.instance.consoleSender.sendMessage("manager.command.delete.group.not-exist", "Group %NAME%", name, " does not exist.")
            return
        }
        try {
            CloudLib.instance.getCloudServiceGroupManager().deleteServiceGroup(serviceGroup)
        } catch (e: IllegalStateException) {
            Launcher.instance.consoleSender.sendMessage("manager.command.delete.group.services-running", "Can not delete group %NAME%", name, " while services of this group are running.")
            return
        }
        Launcher.instance.consoleSender.sendMessage("manager.command.delete.group.success", "Group %NAME%", name, " was deleted.")
    }

}