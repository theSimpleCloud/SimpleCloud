package eu.thesimplecloud.module.npc.module.command.provider

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.launcher.console.command.provider.ICommandSuggestionProvider

/**
 * Created by MrManHD
 * Class create at 19.06.2023 23:11
 */

class ServiceGroupWithoutProxiesCommandSuggestionProvider : ICommandSuggestionProvider {

    override fun getSuggestions(sender: ICommandSender, fullCommand: String, lastArgument: String): List<String> {
        return CloudAPI.instance.getCloudServiceGroupManager().getAllCachedObjects()
            .filter { it.getServiceType() != ServiceType.PROXY }
            .map { it.getName() }
    }

}