package eu.thesimplecloud.module.proxy.manager.commands

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.module.proxy.extensions.mapToLowerCase
import eu.thesimplecloud.module.proxy.manager.ProxyModule

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 16.03.2020
 * Time: 17:51
 */

@Command("proxy", CommandType.CONSOLE_AND_INGAME, "simplecloud.module.proxy")
class ProxyCommand(val module: ProxyModule): ICommandHandler {

    private val propertyPrefix = "module.proxy.command."

    @CommandSubPath("reload", "Reloads the proxy module")
    fun handleReload(sender: ICommandSender) {
        module.loadConfig()
        sender.sendMessage("${propertyPrefix}reload", "Config reloaded successfully.")
    }

    @CommandSubPath("<proxyName> whitelist add <playerName>", "Adds a player to whitelist")
    fun handleWhitelistAdd(sender: ICommandSender, @CommandArgument("proxyName") proxyName: String,
                           @CommandArgument("playerName") playerName: String) {
        val proxyConfiguration = module.getProxyConfiguration(proxyName)

        if (proxyConfiguration == null) {
            sender.sendMessage("${propertyPrefix}whitelist.proxy-not-found",
                    "No configuration for this proxy found.")
            return
        }

        if (proxyConfiguration.whitelist.mapToLowerCase().contains(playerName.toLowerCase())) {
            sender.sendMessage("${propertyPrefix}whitelist.already-whitelisted",
                    "This player is already whitelisted.")
            return
        }

        proxyConfiguration.whitelist.add(playerName)

        module.config.update()
        module.saveConfig()

        sender.sendMessage("${propertyPrefix}whitelist.added",
                "Added %player%", playerName ," to the whitelist.")
    }

    @CommandSubPath("<proxyName> whitelist remove <playerName>", "Removes a player from whitelist")
    fun handleWhitelistRemove(sender: ICommandSender, @CommandArgument("proxyName") proxyName: String,
                           @CommandArgument("playerName") playerName: String) {
        val proxyConfiguration = module.getProxyConfiguration(proxyName)

        if (proxyConfiguration == null) {
            sender.sendMessage("${propertyPrefix}whitelist.proxy-not-found",
                    "No configuration for this proxy found.")
            return
        }

        if (!proxyConfiguration.whitelist.mapToLowerCase().contains(playerName.toLowerCase())) {
            sender.sendMessage("${propertyPrefix}whitelist.not-whitelisted",
                    "This player isn't whitelisted.")
            return
        }

        proxyConfiguration.whitelist.remove(playerName)

        module.config.update()
        module.saveConfig()

        sender.sendMessage("${propertyPrefix}whitelist.removed",
                "Removed %player%", playerName ," from the whitelist.")
    }

}