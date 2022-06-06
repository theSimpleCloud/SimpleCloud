package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.console.command.provider.ServiceCommandSuggestionProvider

@Command("setDisplayName", CommandType.CONSOLE_AND_INGAME, "cloud.command.setdisplayname")
class SetDisplayNameCommand : ICommandHandler {

    @CommandSubPath("<service> <displayName>", "Sets the displayname of a service")
    fun handle(
        sender: ICommandSender,
        @CommandArgument("service", ServiceCommandSuggestionProvider::class) service: ICloudService,
        @CommandArgument("displayName") displayName: String
    ) {
        service.setDisplayName(displayName)
        service.update()
        sender.sendProperty("manager.command.setdisplayname.success")
    }

}