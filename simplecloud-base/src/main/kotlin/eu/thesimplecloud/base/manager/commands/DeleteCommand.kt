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
import eu.thesimplecloud.base.manager.serviceversion.ManagerServiceVersionHandler
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.startup.Launcher

@Command("delete", CommandType.CONSOLE, "cloud.command.delete")
class DeleteCommand : ICommandHandler {

    private val templateManager = CloudAPI.instance.getTemplateManager()

    @CommandSubPath("template <name>", "Deletes a template")
    fun deleteTemplate(@CommandArgument("name") name: String) {
        if (templateManager.getTemplateByName(name) == null) {
            Launcher.instance.consoleSender.sendProperty("manager.command.delete.template.not-exist", name)
            return
        }
        if (CloudAPI.instance.getCloudServiceGroupManager().getAllCachedObjects()
                .any { it.getTemplateName().equals(name, true) }
        ) {
            Launcher.instance.consoleSender.sendProperty("manager.command.delete.template.in-use.group", name)
            return
        }
        if (CloudAPI.instance.getCloudServiceManager().getAllCachedObjects()
                .any { it.getTemplateName().equals(name, true) }
        ) {
            Launcher.instance.consoleSender.sendProperty("manager.command.delete.template.in-use.service", name)
            return
        }
        templateManager.deleteTemplate(name)
        Launcher.instance.consoleSender.sendProperty("manager.command.delete.template.success", name)
    }

    @CommandSubPath("group <name>", "Deletes a group")
    fun deleteGroup(@CommandArgument("name") name: String) {
        val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(name)
        if (serviceGroup == null) {
            Launcher.instance.consoleSender.sendProperty("manager.command.delete.group.not-exist", name)
            return
        }
        val result = runCatching { CloudAPI.instance.getCloudServiceGroupManager().delete(serviceGroup) }
        if (result.isFailure) {
            Launcher.instance.consoleSender.sendProperty("manager.command.delete.group.services-running", name)
            return
        }
        Launcher.instance.consoleSender.sendProperty("manager.command.delete.group.success", name)
    }


    @CommandSubPath("wrapper <wrapper>", "Deletes a wrapper")
    fun deleteWrapper(@CommandArgument("wrapper") name: String) {
        val wrapper = CloudAPI.instance.getWrapperManager().getWrapperByName(name)
        if (wrapper == null) {
            Launcher.instance.consoleSender.sendProperty("manager.command.delete.wrapper.not-exist", name)
            return
        }
        if (wrapper.getServicesRunningOnThisWrapper().isNotEmpty()) {
            Launcher.instance.consoleSender.sendProperty("manager.command.delete.wrapper.services-running", name)
            return
        }
        if (CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupsByWrapperName(wrapper.getName())
                .isNotEmpty()
        ) {
            Launcher.instance.consoleSender.sendProperty("manager.command.delete.wrapper.group-must-start", name)
            return
        }

        CloudAPI.instance.getWrapperManager().delete(wrapper)
        Launcher.instance.consoleSender.sendProperty("manager.command.delete.wrapper.success", name)
    }

    @CommandSubPath("serviceVersion <name>", "Delete a Service Version")
    fun createServiceVersion(@CommandArgument("name") name: String) {
        val serviceVersionHandler = CloudAPI.instance.getServiceVersionHandler() as ManagerServiceVersionHandler
        if (!serviceVersionHandler.doesVersionExist(name)) {
            Launcher.instance.consoleSender.sendProperty("manager.command.delete.service-version.not-exist")
            return
        }
        if (serviceVersionHandler.isVersionInUse(name)) {
            Launcher.instance.consoleSender.sendProperty("manager.command.delete.service-version.still-in-use")
            return
        }
        serviceVersionHandler.deleteServiceVersion(name)
        Launcher.instance.consoleSender.sendProperty("manager.command.delete.service-version.deleted", name)
    }


}