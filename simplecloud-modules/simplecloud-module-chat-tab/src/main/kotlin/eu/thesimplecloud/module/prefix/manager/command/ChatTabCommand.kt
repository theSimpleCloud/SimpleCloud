package eu.thesimplecloud.module.prefix.manager.command

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.console.command.provider.ServiceGroupCommandSuggestionProvider
import eu.thesimplecloud.module.prefix.manager.config.ChatTabConfig
import java.util.*

@Command(name = "chat-tab", CommandType.CONSOLE_AND_INGAME, "cloud.module.chat-tab")
class ChatTabCommand : ICommandHandler {

    @CommandSubPath("set <group> <delay>", "Sets the delay for the given group")
    fun executeSetDelayWithGroup(
        commandSender: ICommandSender,
        @CommandArgument("group", ServiceGroupCommandSuggestionProvider::class) group: String,
        @CommandArgument("delay") delay: String
    ) {

        if (CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(group) == null) {
            commandSender.sendProperty("module.chat-tab.command.chat-tab.group-not-exist")
            return
        }

        val config = ChatTabConfig.getConfig()
        config.delay[group] = delay.toLongOrNull() ?: 0L
        config.update()

        commandSender.sendProperty("module.chat-tab.command.chat-tab.delay-set-success", group, delay)
    }

    @CommandSubPath("set <delay>", "Sets the delay for the current group you're online on")
    fun executeSetDelayWithPlayerGroup(
        commandSender: ICommandSender,
        @CommandArgument("delay") delay: String
    ) {

        if (commandSender !is ICloudPlayer) {
            commandSender.sendProperty("module.chat-tab.command.chat-tab.no-player")
            return
        }

        executeSetDelayWithGroup(commandSender, commandSender.getConnectedServer()!!.getGroupName(), delay)
    }
}

