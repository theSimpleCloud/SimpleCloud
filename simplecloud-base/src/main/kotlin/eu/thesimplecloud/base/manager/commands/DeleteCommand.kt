/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
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
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher

@Command("delete", CommandType.CONSOLE, "cloud.command.delete")
class DeleteCommand : ICommandHandler {

    private val templateManager = CloudAPI.instance.getTemplateManager()

    @CommandSubPath("template <name>", "Deletes a template")
    fun deleteTemplate(@CommandArgument("name") name: String) {
        if (templateManager.getTemplateByName(name) == null) {
            Launcher.instance.consoleSender.sendMessage("manager.command.delete.template.not-exist", "Template %NAME%", name, " does not exist.")
            return
        }
        if (CloudAPI.instance.getCloudServiceGroupManager().getAllCachedObjects().any { it.getTemplateName().equals(name, true) }) {
            Launcher.instance.consoleSender.sendMessage("manager.command.delete.template.in-use.group", "Template %NAME%", name, " is in use by registered service groups. Delete them first.")
            return
        }
        if (CloudAPI.instance.getCloudServiceManager().getAllCachedObjects().any { it.getTemplateName().equals(name, true) }) {
            Launcher.instance.consoleSender.sendMessage("manager.command.delete.template.in-use.service", "Template %NAME%", name, " is in use by registered services. Stop them first.")
            return
        }
        templateManager.deleteTemplate(name)
        Launcher.instance.consoleSender.sendMessage("manager.command.delete.template.success", "Template %NAME%", name, " was deleted.")
    }

    @CommandSubPath("group <name>", "Deletes a group")
    fun deleteGroup(@CommandArgument("name") name: String) {
        val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(name)
        if (serviceGroup == null) {
            Launcher.instance.consoleSender.sendMessage("manager.command.delete.group.not-exist", "Group %NAME%", name, " does not exist.")
            return
        }
        val result = runCatching { CloudAPI.instance.getCloudServiceGroupManager().delete(serviceGroup) }
        if (result.isFailure) {
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