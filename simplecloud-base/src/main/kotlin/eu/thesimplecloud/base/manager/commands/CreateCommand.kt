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
import eu.thesimplecloud.api.template.impl.DefaultTemplate
import eu.thesimplecloud.base.manager.setup.WrapperSetup
import eu.thesimplecloud.base.manager.setup.groups.LobbyGroupSetup
import eu.thesimplecloud.base.manager.setup.groups.ProxyGroupSetup
import eu.thesimplecloud.base.manager.setup.groups.ServerGroupSetup
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher

@Command("create", CommandType.CONSOLE, "cloud.command.create")
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
        templateManager.update(template)
        Launcher.instance.consoleSender.sendMessage("manager.command.create.template.success", "Template %NAME%", name, " created")

        //---create directories
        template.getDirectory().mkdirs()
    }



}