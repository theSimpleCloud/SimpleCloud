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
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath

/**
 * Created by MrManHD
 * Class create at 13.06.2020 17:24
 */

@Command("list", CommandType.CONSOLE_AND_INGAME, "cloud.command.list")
class ListCommand : ICommandHandler {

    @CommandSubPath("", "Lists some information about the cloud")
    fun handleList(commandSender: ICommandSender) {

        CloudAPI.instance.getWrapperManager().getAllCachedObjects().forEach {
            val connectedMessage = if (it.isAuthenticated()) "&aConnected" else "§cNot Connected"
            commandSender.sendMessage("&8>> &3" + it.getName() + " &8(&f" + it.getUsedMemory() + "&8/&f"
                    + it.getMaxMemory()+ "MB&8 | " + connectedMessage + "&8)")
        }

        commandSender.sendMessage(" ")

        val cloudServices = CloudAPI.instance.getCloudServiceManager().getAllCachedObjects()
        val cloudServiceGroups = CloudAPI.instance.getCloudServiceGroupManager().getAllCachedObjects()
        cloudServiceGroups.filter { it.getAllServices().isNotEmpty() }.forEach { groups ->
            val serviceName = if (groups.getRegisteredServiceCount() == 1) "Service" else "Services"

            commandSender.sendMessage("&8>> &7" + groups.getName() + " &8(&f" + groups.getMaxMemory() + "MB &8/&f "
                    + groups.getRegisteredServiceCount() + " " + serviceName + "&8)")

            groups.getAllServices().forEach {
                val wrapperDesign = if (it.getWrapperName() != null) " &8|§3 " + it.getWrapperName() else ""
                commandSender.sendMessage("&8- &b" + it.getName() + " &8(&f" + it.getOnlineCount() + "&8/&f"
                        + it.getMaxPlayers() + " &8|&3 " + it.getState() + wrapperDesign + "&8)")
            }

            commandSender.sendMessage(" ")
        }

        val unusedGroups = cloudServiceGroups.filter { it.getAllServices().isEmpty() }
                .joinToString("&8,&f ") { it.getName() }

        val maxMemory = CloudAPI.instance.getWrapperManager().getAllCachedObjects().sumBy { it.getMaxMemory() }
        val usedMemory = CloudAPI.instance.getWrapperManager().getAllCachedObjects().sumBy { it.getUsedMemory() }

        if (unusedGroups.isNotEmpty()) commandSender.sendMessage("&8>>&7 UnusedGroups&8:&f " + unusedGroups)
        commandSender.sendMessage("&8>>&7 Online Services&8:&f " + cloudServices.size)
        commandSender.sendMessage("&8>>&7 Memory&8:&f " + usedMemory + "&8/&f" + maxMemory + "MB")

    }

}