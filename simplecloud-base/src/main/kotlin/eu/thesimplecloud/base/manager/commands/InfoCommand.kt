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
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.extension.sendMessage

@Command("info", CommandType.CONSOLE, "cloud.command.info")
class InfoCommand : ICommandHandler {

    @CommandSubPath("wrapper <name>", "Prints some information about the specified wrapper")
    fun wrapper(commandSender: ICommandSender, @CommandArgument("name") name: String) {
        val wrapper = CloudAPI.instance.getWrapperManager().getWrapperByName(name)
        if (wrapper == null) {
            commandSender.sendMessage("manager.command.info.wrapper.not-exist", "The specified wrapper does not exist.")
            return
        }
        commandSender.sendMessage(wrapper.toString())
    }


    @CommandSubPath("service <name>", "Prints some information about the specified service")
    fun service(commandSender: ICommandSender, @CommandArgument("name") name: String) {
        val service = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(name)
        if (service == null) {
            commandSender.sendMessage("manager.command.info.service.not-exist", "The specified service does not exist.")
            return
        }
        commandSender.sendMessage(service.toString())
    }

    @CommandSubPath("group <name>", "Prints some information about the specified group")
    fun group(commandSender: ICommandSender, @CommandArgument("name") name: String) {
        val group = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(name)
        if (group == null) {
            commandSender.sendMessage("manager.command.info.group.not-exist", "The specified group does not exist.")
            return
        }
        commandSender.sendMessage(group.toString())
    }

    @CommandSubPath("player <name>", "Prints some information about the specified player")
    fun player(commandSender: ICommandSender, @CommandArgument("name") name: String) {
        val player = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(name)
        if (player == null) {
            commandSender.sendMessage("manager.command.info.player.not-exist", "The specified player does not exist.")
            return
        }
        commandSender.sendMessage(player.toString())
    }

    @CommandSubPath("onlinecount", "Prints the number of online players")
    fun handlePlayers(commandSender: ICommandSender) {
        val onlineCount = CloudAPI.instance.getCloudPlayerManager().getAllCachedObjects().size
        commandSender.sendMessage("manager.command.info.onlinecount", "Online count: %COUNT%", onlineCount.toString())
    }


}