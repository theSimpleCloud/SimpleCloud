/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath

/**
 * Created by MrManHD
 * Class create at 13.06.2020 17:24
 */

@Command("list", CommandType.CONSOLE_AND_INGAME, "cloud.command.list")
class ListCommand : ICommandHandler {

    @CommandSubPath("", "Lists some information about the cloud")
    fun handleList(commandSender: ICommandSender) {
        val darkChatColor = if (commandSender is ICloudPlayer) "&8" else "&7"

        CloudAPI.instance.getWrapperManager().getAllCachedObjects().forEach {
            val connectedMessage = if (it.isAuthenticated()) "&aConnected" else "§cNot Connected"
            commandSender.sendMessage(
                darkChatColor + ">> &3" + it.getName() + darkChatColor + " (&f" + it.getUsedMemory()
                        + darkChatColor + "/&f"
                        + it.getMaxMemory() + "MB" + darkChatColor + " | " + connectedMessage + darkChatColor + ")"
            )
        }

        commandSender.sendMessage(" ")

        val cloudServices = CloudAPI.instance.getCloudServiceManager().getAllCachedObjects()
        val cloudServiceGroups = CloudAPI.instance.getCloudServiceGroupManager().getAllCachedObjects()
        cloudServiceGroups.filter { it.getAllServices().isNotEmpty() }.forEach { groups ->
            val serviceName = if (groups.getRegisteredServiceCount() == 1) "Service" else "Services"

            commandSender.sendMessage(
                darkChatColor + ">> &7" + groups.getName() + darkChatColor + " (&f" + groups.getMaxMemory()
                        + "MB " + darkChatColor + "/&f "
                        + groups.getRegisteredServiceCount() + " " + serviceName + darkChatColor + ")"
            )

            groups.getAllServices().forEach {
                val wrapperDesign =
                    if (it.getWrapperName() != null) " " + darkChatColor + "|&3 " + it.getWrapperName() else ""
                commandSender.sendMessage(
                    darkChatColor + "- &b" + it.getName() + " " + darkChatColor + "(&f"
                            + it.getUsedMemory() + "MB" + " ${darkChatColor}| "
                            + "&f" + it.getOnlineCount()
                            + darkChatColor + "/&f"
                            + it.getMaxPlayers() + " " + darkChatColor + "|&3 " + it.getState() + wrapperDesign + darkChatColor + ")"
                )
            }

            commandSender.sendMessage(" ")
        }

        val unusedGroups = cloudServiceGroups.filter { it.getAllServices().isEmpty() }
            .joinToString(darkChatColor + ",&f ") { it.getName() }

        val maxMemory = CloudAPI.instance.getWrapperManager().getAllCachedObjects().sumBy { it.getMaxMemory() }
        val usedMemory = CloudAPI.instance.getWrapperManager().getAllCachedObjects().sumBy { it.getUsedMemory() }

        if (unusedGroups.isNotEmpty()) commandSender.sendMessage(
            darkChatColor + ">>&7 Unused Groups"
                    + darkChatColor + ":&f " + unusedGroups
        )
        commandSender.sendMessage(darkChatColor + ">>&7 Online Services" + darkChatColor + ":&f " + cloudServices.size)
        commandSender.sendMessage(
            darkChatColor + ">>&7 Memory" + darkChatColor + ":&f " + usedMemory
                    + darkChatColor + "/&f" + maxMemory + "MB"
        )

    }

}