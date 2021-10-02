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
import eu.thesimplecloud.launcher.console.command.provider.ServiceCommandSuggestionProvider
import eu.thesimplecloud.launcher.console.command.provider.ServiceGroupCommandSuggestionProvider
import eu.thesimplecloud.launcher.console.command.provider.WrapperCommandSuggestionProvider
import eu.thesimplecloud.launcher.extension.replace

@Command("info", CommandType.CONSOLE_AND_INGAME, "cloud.command.info")
class InfoCommand : ICommandHandler {

    @CommandSubPath("wrapper <name>", "Prints some information about the specified wrapper")
    fun wrapper(commandSender: ICommandSender, @CommandArgument("name", WrapperCommandSuggestionProvider::class) name: String) {
        val wrapper = CloudAPI.instance.getWrapperManager().getWrapperByName(name)
        if (wrapper == null) {
            commandSender.sendProperty("manager.command.info.wrapper.not-exist")
            return
        }

        val message = wrapper.toString().split("\n")
            .map { it.replace("{","") }
            .map { it.replace("}","") }
            .map { it.replace("\"","") }
            .map { it.replace("true","§a✔") }
            .map { it.replace("false","§c✗") }
            .map { it.replace("authenticated:","§7Authenticated§8:§3") }
            .map { it.replace("usedMemory:","§cMemory§8:§3") }
            .map { it.replace("templatesReceived:","§7Template synchronization§8:").replace("true","§aErfolgreich§f").replace("false","§cFehlgeschlagen") }
            .map { it.replace("currentlyStartingServices:","§7Current starting services§8:§3").replace(",","") }
            .map { it.replace("cpuUsage:","§7CPU§8:§3") }
            .map { it.replace("name:","§7Name§8:§3") }
            .map { it.replace("host:","§7Host§8:§3") }
            .map { it.replace("maxSimultaneouslyStartingServices:","§7Max simultaneously starting services§8:§3") }
            .map { it.replace("maxMemory:","§cMaxMemory§8:§3") }

        commandSender.sendMessage("§7Wrapper§8: §3" + wrapper.getName() + message.joinToString("\n").replace("\"",""))
    }


    @CommandSubPath("service <name>", "Prints some information about the specified service")
    fun service(commandSender: ICommandSender, @CommandArgument("name", ServiceCommandSuggestionProvider::class) name: String) {
        val service = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(name)
        if (service == null) {
            commandSender.sendProperty("manager.command.info.service.not-exist")
            return
        }

        val message = service.toString().split("\n")
            .map { it.replace("}","") }
            .map { it.replace("{","") }
            .map { it.replace(",","") }
            .map { it.replace("\"","") }
            .map { it.replace("true","§a✔") }
            .map { it.replace("false","§c✗") }
            .map { it.replace("serviceState:","§7ServiceState§8:§3") }
            .map { it.replace("PREPARED","§3Prepared") }
            .map { it.replace("STARTING","§3Starting") }
            .map { it.replace("VISIBLE","§3Visible") }
            .map { it.replace("INVISIBLE","§3Invisible") }
            .map { it.replace("CLOSED","§3Closed") }
            .map { it.replace("onlineCount:","§7OnlineCount§8:§3") }
            .map { it.replace("usedMemory:","§cMemory§8:§3") }
            .map { it.replace("authenticated:","§7Authenticated§8:§3") }
            .map { it.replace("propertyMap:","§7PropertyMap§8:§3") }
            .map { it.replace("groupName:","§7GroupName§8:§3") }
            .map { it.replace("serviceNumber:","§7ServiceNumber§8:§3") }
            .map { it.replace("uniqueId:","§7uniqueId§8:§3") }
            .map { it.replace("templateName:","§7TemplateName§8:§3") }
            .map { it.replace("wrapperName:","§7WrapperName§8:§3") }
            .map { it.replace("port:","§7Port§8:§3") }
            .map { it.replace("maxMemory:","§cMaxMemory§8:§3") }
            .map { it.replace("maxPlayers:","§7MaxPlayer§8:§3") }
            .map { it.replace("motd:","§7Motd§8:§3") }
            .map { it.replace("serviceVersion:","§7ServiceVersion§8:§3") }
            .map { it.replace("name:","§7Name§8:§3") }
            .map { it.replace("serviceAPIType:","§7serverAPIType§8:§3") }
            .map { it.replace("downloadURL:","§7DownloadUrl§8:§3") }


        commandSender.sendMessage("§7Service§8: §3" + service.getName() + message.joinToString("\n"))
    }


    @CommandSubPath("group <name>", "Prints some information about the specified group")
    fun group(commandSender: ICommandSender, @CommandArgument("name", ServiceGroupCommandSuggestionProvider::class) name: String) {
        val group = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(name)
        if (group == null) {
            commandSender.sendProperty("manager.command.info.group.not-exist")
            return
        }

        val message = group.toString().split("\n")
            .map { it.replace("}","") }
            .map { it.replace("{","") }
            .map { it.replace(",","") }
            .map { it.replace("\"","") }
            .map { it.replace("true","§a✔") }
            .map { it.replace("false","§c✗") }
            .map { it.replace("priority:","§7Priority§8:§3") }
            .map { it.replace("startPort:","§7StartPort§8:§3") }
            .map { it.replace("hiddenAtProxyGroups:","§7HiddenAtProxyGroup§8:§3") }
            .map { it.replace("serviceVersion:","§7ServiceVersion§8:§3") }
            .map { it.replace("name:","§7Name§8:§3") }
            .map { it.replace("templateName:","§7TemplateName§8:§3") }
            .map { it.replace("maxMemory:","§cMaxMemory§8:§3") }
            .map { it.replace("maxPlayers:","§7MaxPlayer§8:§3") }
            .map { it.replace("minimumOnlineServiceCount:","§7MinimumOnlineServiceCount§8:§3") }
            .map { it.replace("maximumOnlineServiceCount:","§7MaximumOnlineServiceCount§8:§3") }
            .map { it.replace("maintenance:","§7Maintenance§8:§3") }
            .map { it.replace("static:","§7Static§8:§3") }
            .map { it.replace("percentToStartNewService:","§7PercentToStartNewService§8:§3") }
            .map { it.replace("wrapperName:","§7WrapperName§8:§3") }
            .map { it.replace("startPriority:","§7StartPriority§8:§3") }
            .map { it.replace("permission:","§7Permission§8:§3").replace("null","§c✗") }
            .map { it.replace("stateUpdating:","§7StateUpdating§8:§3") }


        commandSender.sendMessage("§7Group§8: §3" + group.getName() + message.joinToString("\n"))
    }


    @CommandSubPath("player <name>", "Prints some information about the specified player")
    fun player(commandSender: ICommandSender, @CommandArgument("name") name: String) {
        val player = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(name)
        if (player == null) {
            commandSender.sendProperty("manager.command.info.player.not-exist")
            return
        }

        val message = player.toString().split("\n")
            .map { it.replace("}","") }
            .map { it.replace("{","") }
            .map { it.replace(",","") }
            .map { it.replace("\"","") }
            .map { it.replace("true","§a✔") }
            .map { it.replace("false","§c✗") }
            .map { it.replace("CONNECTED","§a✔") }
            .map { it.replace("hostname:","§7PlayerIP§8:§3") }
            .map { it.replace("connectState:","§7ConnectState§8:") }
            .map { it.replace("online:","§7Online§8:") }
            .map { it.replace("updatesEnabled:","§7UpdatesEnabled§8:") }
            .map { it.replace("connectedProxyName:","§7ConnectedProxyName§8:§3") }
            .map { it.replace("connectedServerName:","§7ConnectedServerName§8:§3") }
            .map { it.replace("displayName:","§7DisplayName§8:§3") }
            .map { it.replace("name:","§7Name§8:§3") }
            .map { it.replace("firstLogin:","§7FirstLogin§8:§3") }
            .map { it.replace("lastLogin:","§7LastLogin§8:§3") }
            .map { it.replace("onlineTime:","§7OnlineTime§8:§3") }
            .map { it.replace("lastPlayerConnection:","§7LastPlayerConnection§8:§3") }
            .map { it.replace("address:","§7Adress§8:") }
            .map { it.replace("port:","§7Port§8:§3") }
            .map { it.replace("uniqueId:","§7UniqueId§8:§3") }
            .map { it.replace("onlineMode:","§7OnlineMode§8:§3") }
            .map { it.replace("version:","§7Version§8:§3") }
            .map { it.replace("propertyMap:","§7PropertyMap§8:§3") }
            .map { it.replace("simplecloud-module-permission-player:","§7SimpleCloud-Module-Permission-Player§8:§3") }
            .map { it.replace("className:","§7ClassName§8:§3") }
            .map { it.replace("valueAsString:","§7ValueAsString§8:§3") }


        commandSender.sendMessage("§7Player§8: §3" + player.getName() + "§8:" + message.joinToString("\n"))
    }



    @CommandSubPath("onlinecount", "Prints the number of online players")
    fun handlePlayers(commandSender: ICommandSender) {
        val onlineCount = CloudAPI.instance.getCloudPlayerManager().getAllCachedObjects().size
        commandSender.sendProperty("manager.command.info.onlinecount", onlineCount.toString())

    }

}
