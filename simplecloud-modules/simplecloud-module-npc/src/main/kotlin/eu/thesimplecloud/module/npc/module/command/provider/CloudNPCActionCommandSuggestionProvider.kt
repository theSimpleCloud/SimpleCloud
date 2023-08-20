package eu.thesimplecloud.module.npc.module.command.provider

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.provider.ICommandSuggestionProvider
import eu.thesimplecloud.module.npc.lib.config.npc.action.Action

class CloudNPCActionCommandSuggestionProvider: ICommandSuggestionProvider {
    override fun getSuggestions(sender: ICommandSender, fullCommand: String, lastArgument: String): List<String> {
        return Action.values().map { it.name }
    }
}