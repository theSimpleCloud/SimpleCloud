package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.console.command.provider.ServiceGroupCommandSuggestionProvider
import eu.thesimplecloud.launcher.extension.sendMessage

@Command("startStatic", CommandType.CONSOLE_AND_INGAME, "cloud.command.startstatic")
class StartStaticCommand : ICommandHandler {

    @CommandSubPath("<service>")
    fun handleStartStatic(commandSender: ICommandSender, @CommandArgument("service", ServiceGroupCommandSuggestionProvider::class) serviceName: String) {
        val runningService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(serviceName)
        if (runningService != null) {
            commandSender.sendMessage("manager.command.startstatic.service-already-online",
                    "&cThe specified service is already running")
            return
        }
        val groupName = serviceName.split("-").dropLast(1).joinToString("-")
        val numberString = serviceName.split("-").last()
        val number  = kotlin.runCatching { numberString.toInt() }.getOrNull()
        val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(groupName)
        if (number == null || serviceGroup == null) {
            commandSender.sendMessage("manager.command.startstatic.service-invalid",
                    "&cThe specified service is invalid")
            return
        }

        if (!serviceGroup.isStatic()) {
            commandSender.sendMessage("manager.command.startstatic.group-not-static",
                    "&cThe specified service group must be static")
            return
        }

        Manager.instance.serviceHandler.startService(serviceGroup, serviceGroup.getTemplate(), number, serviceGroup.getMaxMemory())
        commandSender.sendMessage("manager.command.startstatic.success",
                "Starting service...")
    }

}

