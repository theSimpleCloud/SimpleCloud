package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.console.command.provider.ServiceGroupCommandSuggestionProvider
import eu.thesimplecloud.launcher.extension.sendMessage

@Command("start", CommandType.CONSOLE_AND_INGAME, "cloud.command.start")
class StartCommand : ICommandHandler {

    @CommandSubPath("<group>", "Starts a service")
    fun startService(commandSender: ICommandSender, @CommandArgument("group", ServiceGroupCommandSuggestionProvider::class) cloudServiceGroup: ICloudServiceGroup) {
        startService(commandSender, cloudServiceGroup, 1)
    }

    @CommandSubPath("<group> <count>", "Starts a service")
    fun startService(
            commandSender: ICommandSender,
            @CommandArgument("group", ServiceGroupCommandSuggestionProvider::class) cloudServiceGroup: ICloudServiceGroup,
            @CommandArgument("count") count: Int
    ) {
        for (i in 0 until count) {
            cloudServiceGroup.startNewService()
        }
        commandSender.sendMessage("manager.command.start.success", "Trying to start %COUNT%", count.toString(), " a new service of group %GROUP%", cloudServiceGroup.getName())
    }

}