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

package eu.thesimplecloud.module.sign.manager.command

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.console.command.provider.ServiceGroupCommandSuggestionProvider
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.module.sign.lib.SignModuleConfig
import eu.thesimplecloud.module.sign.manager.SignsModule

@Command("signs", CommandType.CONSOLE_AND_INGAME, "cloud.command.signs")
class SignsCommand : ICommandHandler {

    @CommandSubPath("reload", "Reloads the config")
    fun handleReload(commandSender: ICommandSender) {
        SignsModule.INSTANCE.reloadConfig()
        commandSender.sendMessage("manager.command.signs.reload", "&aConfig reloaded.")
    }

    @CommandSubPath("layouts", "Lists all layouts")
    fun handleLayouts(commandSender: ICommandSender) {
        val names = SignModuleConfig.INSTANCE.obj.signLayouts.map { it.name }
                .filter { it != "SEARCHING" }
                .filter { it != "STARTING" }
                .filter { it != "MAINTENANCE" }

        commandSender.sendMessage("&eLayouts&8: &7${names.joinToString()}")
    }

    @CommandSubPath("group <group> layout <layout>", "Sets the layout for this group.")
    fun handleLayout(commandSender: ICommandSender, @CommandArgument("group", ServiceGroupCommandSuggestionProvider::class) groupName: String, @CommandArgument("layout") layoutName: String) {
        val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(groupName)
        if (serviceGroup == null) {
            commandSender.sendMessage("manager.command.signs.group-not-found", "§cGroup not found.")
            return
        }
        val signModuleConfig = SignModuleConfig.INSTANCE.obj
        val signLayout = signModuleConfig.getSignLayoutByName(layoutName)
        if (signLayout == null) {
            commandSender.sendMessage("manager.command.signs.layout-not-found", "§cLayout not found.")
            return
        }
        signModuleConfig.groupToLayout.putGroupToLayout(serviceGroup.getName(), signLayout.name)
        signModuleConfig.update()
        commandSender.sendMessage("manager.command.signs.layout-set", "§7Group &e%GROUP%", serviceGroup.getName(), " &7is now using the layout &e%LAYOUT%", signLayout.name, "&7.")
    }

}