/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.parser.string.StringParser
import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.utils.getAllFieldsFromClassAndSubClasses
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.base.manager.commands.provider.EditGroupParameterCommandSuggestionProvider
import eu.thesimplecloud.base.manager.commands.provider.EditWrapperParameterCommandSuggestionProvider
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.console.command.provider.ServiceGroupCommandSuggestionProvider
import eu.thesimplecloud.launcher.console.command.provider.TemplateCommandSuggestionProvider
import eu.thesimplecloud.launcher.console.command.provider.WrapperCommandSuggestionProvider
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher
import java.lang.reflect.Field

@Command("edit", CommandType.CONSOLE_AND_INGAME, "cloud.command.edit")
class EditCommand : ICommandHandler {

    //group

    @CommandSubPath("group <name> <parameter>", "Shows the parameters value")
    fun editGroup(commandSender: ICommandSender, @CommandArgument("name", ServiceGroupCommandSuggestionProvider::class) name: String, @CommandArgument("parameter", EditGroupParameterCommandSuggestionProvider::class) parameter: String) {
        val fields = getFieldsOfGroup(name) ?: return
        val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(name)!!
        val lowerCaseFieldNames = fields.map { it.name.toLowerCase() }
        if (lowerCaseFieldNames.contains(parameter.toLowerCase())) {
            val field = fields[lowerCaseFieldNames.indexOf(parameter.toLowerCase())]
            field.isAccessible = true
            val fieldValue = field[serviceGroup]
            commandSender.sendMessage("Value: $fieldValue")
        } else {
            sendAllParameters(commandSender, fields)
        }
    }

    @CommandSubPath("group <name> <parameter> <value>", "Edits a service group")
    fun editGroup(commandSender: ICommandSender, @CommandArgument("name", ServiceGroupCommandSuggestionProvider::class) name: String, @CommandArgument("parameter", EditGroupParameterCommandSuggestionProvider::class) parameter: String, @CommandArgument("value") value: String) {
        val fields = getFieldsOfGroup(name) ?: return
        val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(name)!!
        val lowerCaseFieldNames = fields.map { it.name.toLowerCase() }
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
                CloudAPI.instance.getCloudServiceGroupManager().update(serviceGroup)
            } catch (e: Exception) {
                commandSender.sendMessage("manager.command.edit.group.invalid-value", "Invalid value. Expected type: %TYPE%", field.type.simpleName)
                return
            }
        } else {
            sendAllParameters(commandSender, fields)
        }
    }

    fun getFieldsOfGroup(groupName: String): List<Field>? {
        val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(groupName)
        if (serviceGroup == null) {
            Launcher.instance.consoleSender.sendMessage("manager.command.edit.group.not-exist", "The specified group does not exist.")
            return null
        }
        val allFields = serviceGroup::class.java.getAllFieldsFromClassAndSubClasses().filter { !Collection::class.java.isAssignableFrom(it.type) }
        return allFields.filterNot { it.name == "name" || it.name == "serviceVersion" || it.name == "wrapperName" }
    }
    //wrapper

    @CommandSubPath("wrapper <name> <parameter>", "Shows the parameters value")
    fun editWrapper(commandSender: ICommandSender, @CommandArgument("name", WrapperCommandSuggestionProvider::class) wrapper: IWrapperInfo, @CommandArgument("parameter", EditWrapperParameterCommandSuggestionProvider::class) parameter: String) {
        val fields = getFieldsOfWrapper(wrapper) ?: return
        val lowerCaseFieldNames = fields.map { it.name.toLowerCase() }
        if (lowerCaseFieldNames.contains(parameter.toLowerCase())) {
            val field = fields[lowerCaseFieldNames.indexOf(parameter.toLowerCase())]
            field.isAccessible = true
            val fieldValue = field[wrapper]
            commandSender.sendMessage("Value: $fieldValue")
        } else {
            sendAllParameters(commandSender, fields)
        }
    }

    @CommandSubPath("wrapper <name> <parameter> <value>", "Edits a wrapper")
    fun editWrapper(commandSender: ICommandSender, @CommandArgument("name", WrapperCommandSuggestionProvider::class) wrapper: IWrapperInfo, @CommandArgument("parameter", EditWrapperParameterCommandSuggestionProvider::class) parameter: String, @CommandArgument("value") value: String) {
        val fields = getFieldsOfWrapper(wrapper) ?: return
        val lowerCaseFieldNames = fields.map { it.name.toLowerCase() }
        if (lowerCaseFieldNames.contains(parameter.toLowerCase())) {
            val field = fields[lowerCaseFieldNames.indexOf(parameter.toLowerCase())]
            field.isAccessible = true
            val type = StringParser().parseToObject(value, field.type)
            if (type == null) {
                commandSender.sendMessage("manager.command.edit.wrapper.invalid-value", "Invalid value. Expected type: %TYPE%", field.type.simpleName)
                return
            }
            try {
                field.set(wrapper, type)
                commandSender.sendMessage("manager.command.edit.wrapper.success", "Wrapper edited.")
                CloudAPI.instance.getWrapperManager().update(wrapper)
            } catch (e: Exception) {
                commandSender.sendMessage("manager.command.edit.wrapper.invalid-value", "Invalid value. Expected type: %TYPE%", field.type.simpleName)
                return
            }
        } else {
            sendAllParameters(commandSender, fields)
        }
    }

    private fun getFieldsOfWrapper(wrapper: IWrapperInfo): List<Field>? {
        val allFields = wrapper::class.java.getAllFieldsFromClassAndSubClasses().filter { !Collection::class.java.isAssignableFrom(it.type) }
        return allFields.filterNot { it.name == "name" || it.name == "host" }
    }

    private fun sendAllParameters(commandSender: ICommandSender, fields: List<Field>) {
        commandSender.sendMessage("manager.command.edit.allowed-parameters", "Allowed parameters are:")
        commandSender.sendMessage(fields.joinToString { it.name })
    }

    @CommandSubPath("template <name> inheritance add <otherTemplate>", "Adds a inheritance to a template")
    fun addInheritTemplate(commandSender: ICommandSender, @CommandArgument("name", TemplateCommandSuggestionProvider::class) template: ITemplate, @CommandArgument("otherTemplate", TemplateCommandSuggestionProvider::class) otherTemplate: ITemplate) {
        if (template == otherTemplate) {
            commandSender.sendMessage("manager.command.edit.template.inheritance.add.both-equal", "Cannot add a template as inheritance to itself.")
            return
        }
        if (template.getInheritedTemplateNames().contains(otherTemplate.getName())) {
            commandSender.sendMessage("manager.command.edit.template.inheritance.add.already-added", "Template %NAME%", template.getName(), " is already inheriting from %OTHER_NAME%", otherTemplate.getName())
            return
        }
        template.addInheritanceTemplate(otherTemplate)
        CloudAPI.instance.getTemplateManager().update(template)
        commandSender.sendMessage("manager.command.edit.template.inheritance.add.success", "Template %NAME%", template.getName(), " is now inheriting from %OTHER_NAME%", otherTemplate.getName())
    }

    @CommandSubPath("template <name> inheritance remove <otherTemplate>", "Removes a inheritance from a template")
    fun removeInheritTemplate(commandSender: ICommandSender, @CommandArgument("name", TemplateCommandSuggestionProvider::class) template: ITemplate, @CommandArgument("otherTemplate", TemplateCommandSuggestionProvider::class) otherTemplate: ITemplate) {
        if (!template.getInheritedTemplateNames().contains(otherTemplate.getName())) {
            commandSender.sendMessage("manager.command.edit.template.inheritance.remove.not-added", "Template %NAME%", template.getName(), " is not inheriting from %OTHER_NAME%", otherTemplate.getName())
            return
        }
        template.removeInheritanceTemplate(otherTemplate)
        CloudAPI.instance.getTemplateManager().update(template)
        commandSender.sendMessage("manager.command.edit.template.inheritance.remove.success", "Template %NAME%", template.getName(), " is no longer inheriting from %OTHER_NAME%", otherTemplate.getName())
    }


    @CommandSubPath("template <name> module add <module>", "Adds a module to a template")
    fun addModuleNameToCopy(commandSender: ICommandSender, @CommandArgument("name", TemplateCommandSuggestionProvider::class) template: ITemplate, @CommandArgument("module") module: String) {
        if (template.getModuleNamesToCopy().map { it.toLowerCase() }.contains(module)) {
            commandSender.sendMessage("manager.command.edit.template.modules.add.already-added", "Module %MODULE%", module, " is already added to template %TEMPLATE%", template.getName())
            return
        }
        template.addModuleNameToCopy(module)
        CloudAPI.instance.getTemplateManager().update(template)
        commandSender.sendMessage("manager.command.edit.template.modules.add.success", "Added module %MODULE%", module, " to template %TEMPLATE%", template.getName())
    }

    @CommandSubPath("template <name> module remove <module>", "Removes a module from a template")
    fun removeModuleNameToCopy(commandSender: ICommandSender, @CommandArgument("name", TemplateCommandSuggestionProvider::class) template: ITemplate, @CommandArgument("module") module: String) {
        if (!template.getModuleNamesToCopy().map { it.toLowerCase() }.contains(module)) {
            commandSender.sendMessage("manager.command.edit.template.modules.add.not-added", "Module %MODULE%", module, " was not added to template %TEMPLATE%", template.getName())
            return
        }
        template.removeModuleNameToCopy(module)
        CloudAPI.instance.getTemplateManager().update(template)
        commandSender.sendMessage("manager.command.edit.template.inheritance.remove.success", "Module %MODULE%", module, " was removed from template %TEMPLATE%", template.getName())
    }

}