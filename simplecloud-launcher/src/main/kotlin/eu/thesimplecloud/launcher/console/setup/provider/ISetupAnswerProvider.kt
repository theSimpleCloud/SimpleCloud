package eu.thesimplecloud.launcher.console.setup.provider

import eu.thesimplecloud.api.command.ICommandSender

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.06.2020
 * Time: 16:15
 */
interface ISetupAnswerProvider {

    /**
     * Returns the suggestions for an argument
     * @param sender the sender og the tab request
     * @return a list with arguments to suggest
     */
    fun getSuggestions(sender: ICommandSender): Collection<String>

}