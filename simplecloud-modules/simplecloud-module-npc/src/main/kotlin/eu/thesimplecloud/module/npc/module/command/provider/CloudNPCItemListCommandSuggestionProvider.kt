package eu.thesimplecloud.module.npc.module.command.provider

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.provider.ICommandSuggestionProvider
import eu.thesimplecloud.module.npc.lib.type.MaterialType
import eu.thesimplecloud.module.npc.module.NPCModule

class CloudNPCItemListCommandSuggestionProvider: ICommandSuggestionProvider {

    private var types: MaterialType? = null

    override fun getSuggestions(sender: ICommandSender, fullCommand: String, lastArgument: String): List<String> {
        if (this.types == null)
            this.types = NPCModule.instance.getMaterialCollection()

        return this.types!!.types
    }
}