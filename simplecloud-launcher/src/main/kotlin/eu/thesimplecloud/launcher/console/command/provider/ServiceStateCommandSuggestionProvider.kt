package eu.thesimplecloud.launcher.console.command.provider

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.service.ServiceState

class ServiceStateCommandSuggestionProvider : ICommandSuggestionProvider {

    override fun getSuggestions(sender: ICommandSender, fullCommand: String, lastArgument: String): List<String> {
        return ServiceState.values().map { it.name.uppercase() }
    }

}