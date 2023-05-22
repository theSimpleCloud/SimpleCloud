package eu.thesimplecloud.module.npc.module.command.provider

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.provider.ICommandSuggestionProvider
import eu.thesimplecloud.module.npc.lib.type.MobType
import eu.thesimplecloud.module.npc.module.NPCModule

class CloudNPCMobTypeCommandSuggestionProvider: ICommandSuggestionProvider {

    private var types: MobType? = null

    override fun getSuggestions(sender: ICommandSender, fullCommand: String, lastArgument: String): List<String> {
        if (this.types == null)
            this.types = NPCModule.instance.getMobCollection()

        if (this.types == null)
            return emptyList()

        return this.types!!.types
    }
}