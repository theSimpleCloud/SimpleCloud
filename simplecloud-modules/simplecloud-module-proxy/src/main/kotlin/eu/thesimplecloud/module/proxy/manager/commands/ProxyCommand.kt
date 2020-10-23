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

package eu.thesimplecloud.module.proxy.manager.commands

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.module.proxy.extensions.mapToLowerCase
import eu.thesimplecloud.module.proxy.manager.ProxyModule

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 16.03.2020
 * Time: 17:51
 */

@Command("proxy", CommandType.CONSOLE_AND_INGAME, "cloud.module.proxy")
class ProxyCommand(val module: ProxyModule) : ICommandHandler {

    private val propertyPrefix = "module.proxy.command."

    @CommandSubPath("reload", "Reloads the proxy module")
    fun handleReload(sender: ICommandSender) {
        module.loadConfig()
        sender.sendProperty("${propertyPrefix}reload")
    }

    @CommandSubPath("<proxyName> whitelist add <playerName>", "Adds a player to whitelist")
    fun handleWhitelistAdd(sender: ICommandSender, @CommandArgument("proxyName") proxyName: String,
                           @CommandArgument("playerName") playerName: String) {
        val proxyConfiguration = module.getProxyConfiguration(proxyName)

        if (proxyConfiguration == null) {
            sender.sendProperty("${propertyPrefix}whitelist.proxy-not-found")
            return
        }

        if (proxyConfiguration.whitelist.mapToLowerCase().contains(playerName.toLowerCase())) {
            sender.sendProperty("${propertyPrefix}whitelist.already-whitelisted")
            return
        }

        proxyConfiguration.whitelist.add(playerName)

        module.config.update()
        module.saveConfig()

        sender.sendProperty("${propertyPrefix}whitelist.added", playerName)
    }

    @CommandSubPath("<proxyName> whitelist remove <playerName>", "Removes a player from whitelist")
    fun handleWhitelistRemove(sender: ICommandSender, @CommandArgument("proxyName") proxyName: String,
                              @CommandArgument("playerName") playerName: String) {
        val proxyConfiguration = module.getProxyConfiguration(proxyName)

        if (proxyConfiguration == null) {
            sender.sendProperty("${propertyPrefix}whitelist.proxy-not-found")
            return
        }

        if (!proxyConfiguration.whitelist.mapToLowerCase().contains(playerName.toLowerCase())) {
            sender.sendProperty("${propertyPrefix}whitelist.not-whitelisted")
            return
        }

        proxyConfiguration.whitelist.remove(playerName)

        module.config.update()
        module.saveConfig()

        sender.sendProperty("${propertyPrefix}whitelist.removed", playerName)
    }

}