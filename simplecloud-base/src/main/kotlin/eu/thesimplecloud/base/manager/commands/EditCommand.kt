package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.parser.string.StringParser
import eu.thesimplecloud.lib.template.ITemplate
import eu.thesimplecloud.lib.utils.getAllFieldsFromClassAndSubClasses
import java.lang.reflect.Field

@Command("edit", false)
class EditCommand : ICommandHandler {

    @CommandSubPath("group <name> <parameter> <value>", "Edits a service group.")
    fun editGroup(commandSender: ICommandSender, @CommandArgument("name") name: String, @CommandArgument("parameter") parameter: String, @CommandArgument("value") value: String){
        val serviceGroup = CloudLib.instance.getCloudServiceGroupManager().getServiceGroup(name)
        if (serviceGroup == null){
            Launcher.instance.consoleSender.sendMessage("manager.command.edit.group.not-exist", "The specified group does not exist.")
            return
        }
        val fields = serviceGroup::class.java.getAllFieldsFromClassAndSubClasses().filter { !Collection::class.java.isAssignableFrom(it.type) }
        val lowerCaseFieldNames = fields.map { it.name }.map { it.toLowerCase() }
        if (lowerCaseFieldNames.contains(parameter.toLowerCase())) {
            val field = fields[lowerCaseFieldNames.indexOf(parameter.toLowerCase())]
            field.isAccessible = true
            val type = StringParser().parseToObject(value, field.type)
            if (type == null) {
                commandSender.sendMessage("manager.command.edit.group.invalid-value", "Invalid value. Expected type: %TYPE%", field.type.simpleName)
                return
            }
            try {
                field.set(serviceGroup, type)
                commandSender.sendMessage("manager.command.edit.group.success", "Group edited.")
                CloudLib.instance.getCloudServiceGroupManager().updateGroup(serviceGroup)
            } catch (e: Exception) {
                commandSender.sendMessage("manager.command.edit.group.invalid-value", "Invalid value. Expected type: %TYPE%", field.type.simpleName)
                return
            }
        } else {
            sendAllParameters(commandSender, fields)
        }
    }

    fun sendAllParameters(commandSender: ICommandSender, fields: List<Field>){
        commandSender.sendMessage("manager.command.edit.group.allowed-parameters", "Allowed parameters are:")
        commandSender.sendMessage(fields.joinToString { it.name })
    }

    @CommandSubPath("template <name> inheritance add <otherTemplate>", "Adds a inheritance to a template")
    fun addInheritTemplate(commandSender: ICommandSender, @CommandArgument("name") template: ITemplate, @CommandArgument("otherTemplate") otherTemplate: ITemplate) {
        if (template == otherTemplate){
            commandSender.sendMessage("manager.command.edit.template.inheritance.add.both-equal", "Cannot add a template as inheritance to itself.")
            return
        }
        if (template.getInheritedTemplateNames().contains(otherTemplate.getName().toLowerCase())){
            commandSender.sendMessage("manager.command.edit.template.inheritance.add.already-added", "Template %NAME%", template.getName(), " is already inheriting from %OTHER_NAME%", otherTemplate.getName())
            return
        }
        template.addInheritanceTemplate(otherTemplate)
        CloudLib.instance.getTemplateManager().updateTemplate(template)
        commandSender.sendMessage("manager.command.edit.template.inheritance.add.success", "Template %NAME%", template.getName(), " is now inheriting from %OTHER_NAME%", otherTemplate.getName())
    }

    @CommandSubPath("template <name> inheritance remove <otherTemplate>", "Removes a inheritance from a template")
    fun removeInheritTemplate(commandSender: ICommandSender, @CommandArgument("name") template: ITemplate, @CommandArgument("otherTemplate") otherTemplate: ITemplate) {
        if (!template.getInheritedTemplateNames().contains(otherTemplate.getName().toLowerCase())){
            commandSender.sendMessage("manager.command.edit.template.inheritance.remove.not-added", "Template %NAME%", template.getName(), " is not inheriting from %OTHER_NAME%", otherTemplate.getName())
            return
        }
        template.removeInheritanceTemplate(otherTemplate)
        CloudLib.instance.getTemplateManager().updateTemplate(template)
        commandSender.sendMessage("manager.command.edit.template.inheritance.remove.success", "Template %NAME%", template.getName(), " is no longer inheriting from %OTHER_NAME%", otherTemplate.getName())
    }

}