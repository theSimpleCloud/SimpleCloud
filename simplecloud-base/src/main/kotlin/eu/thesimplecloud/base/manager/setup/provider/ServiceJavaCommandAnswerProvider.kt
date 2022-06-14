package eu.thesimplecloud.base.manager.setup.provider

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.javaVersions.JavaVersion
import eu.thesimplecloud.launcher.console.setup.provider.ISetupAnswerProvider

class ServiceJavaCommandAnswerProvider : ISetupAnswerProvider {

    override fun getSuggestions(sender: ICommandSender): Collection<String> {
        return JavaVersion.paths.versions.keys.union(listOf("default"))
    }
}