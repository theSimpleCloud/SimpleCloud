package eu.thesimplecloud.launcher.console.command.provider

import eu.thesimplecloud.api.command.ICommandSender

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.04.2020
 * Time: 18:36
 */
interface ICommandSuggestionProvider {

    /**
     * Returns the suggestions for an argument
     * @param sender the sender og the tab request
     * @param fullCommand the full command so far including the last argument
     * @param lastArgument the last argument of the [fullCommand]
     * @return a list with arguments to suggest
     */
    fun getSuggestions(sender: ICommandSender, fullCommand: String, lastArgument: String): List<String>

}