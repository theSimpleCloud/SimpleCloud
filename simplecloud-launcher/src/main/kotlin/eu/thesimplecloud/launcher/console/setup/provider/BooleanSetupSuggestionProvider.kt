package eu.thesimplecloud.launcher.console.setup.provider

import eu.thesimplecloud.api.command.ICommandSender

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.06.2020
 * Time: 16:18
 */
class BooleanSetupSuggestionProvider : ISetupSuggestionProvider {

    override fun getSuggestions(sender: ICommandSender, userInput: String): List<String> {
        return listOf("yes", "no")
    }

}