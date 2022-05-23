package eu.thesimplecloud.base.manager.setup.provider

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.service.version.type.JavaCommandType
import eu.thesimplecloud.launcher.console.setup.provider.ISetupAnswerProvider

class ServiceJavaCommandAnswerProvider : ISetupAnswerProvider {

    override fun getSuggestions(sender: ICommandSender): Collection<String> {
        return JavaCommandType.values().map { it.name }
    }
}