package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.extension.sendPacketToAllAuthenticatedClients
import eu.thesimplecloud.api.network.packets.template.PacketIODeleteTemplate
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.extension.sendMessage

@Command("delete", CommandType.CONSOLE)
class DeleteCommand : ICommandHandler {

    val templateManager = CloudAPI.instance.getTemplateManager()

    @CommandSubPath("template <name>", "Deletes a template")
    fun deleteTemplate(@CommandArgument("name") name: String) {
        if (templateManager.getTemplateByName(name) == null) {
            Launcher.instance.consoleSender.sendMessage("manager.command.delete.template.not-exist", "Template %NAME%", name, " does not exist.")
            return
        }
        if (CloudAPI.instance.getCloudServiceGroupManager().getAllGroups().any { it.getTemplateName().equals(name, true) }) {
            Launcher.instance.consoleSender.sendMessage("manager.command.delete.template.in-use.group", "Template %NAME%", name, " is in use by registered service groups. Delete them first.")
            return
        }
        if (CloudAPI.instance.getCloudServiceManager().getAllCloudServices().any { it.getTemplateName().equals(name, true) }) {
            Launcher.instance.consoleSender.sendMessage("manager.command.delete.template.in-use.service", "Template %NAME%", name, " is in use by registered services. Stop them first.")
            return
        }
        templateManager.removeTemplate(name)
        Manager.instance.communicationServer.getClientManager().sendPacketToAllAuthenticatedClients(PacketIODeleteTemplate(name))
        Launcher.instance.consoleSender.sendMessage("manager.command.delete.template.success", "Template %NAME%", name, " was deleted.")
    }

    @CommandSubPath("group <name>", "Deletes a group")
    fun deleteGroup(@CommandArgument("name") name: String) {
        val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(name)
        if (serviceGroup == null) {
            Launcher.instance.consoleSender.sendMessage("manager.command.delete.group.not-exist", "Group %NAME%", name, " does not exist.")
            return
        }
        if (!CloudAPI.instance.getCloudServiceGroupManager().deleteServiceGroup(serviceGroup).isSuccess) {
            Launcher.instance.consoleSender.sendMessage("manager.command.delete.group.services-running", "Can not delete group %NAME%", name, " while services of this group are running.")
            return
        }
        Launcher.instance.consoleSender.sendMessage("manager.command.delete.group.success", "Group %NAME%", name, " was deleted.")
    }

    /*
    @CommandSubPath("group <wrapper>", "Deletes a wrapper")
    fun deleteWrapper(@CommandArgument("wrapper") name: String) {
        val wrapper = CloudAPI.instance.getWrapperManager().getWrapperByName(name)
        if (wrapper == null) {
            Launcher.instance.consoleSender.sendMessage("manager.command.delete.wrapper.not-exist", "Wrapper %NAME%", name, " does not exist.")
            return
        }
        try {
            CloudAPI.instance.getWrapperManager().remove()
        } catch (e: IllegalStateException) {
            Launcher.instance.consoleSender.sendMessage("manager.command.delete.group.services-running", "Can not delete group %NAME%", name, " while services of this group are running.")
            return
        }
        Launcher.instance.consoleSender.sendMessage("manager.command.delete.group.success", "Group %NAME%", name, " was deleted.")
    }
    */
     

}