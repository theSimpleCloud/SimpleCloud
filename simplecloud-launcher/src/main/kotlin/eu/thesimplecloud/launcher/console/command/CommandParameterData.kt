package eu.thesimplecloud.launcher.console.command

import eu.thesimplecloud.launcher.console.command.provider.ICommandSuggestionProvider

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.09.2019
 * Time: 14:41
 */
class CommandParameterData(val type: Class<*>, val provider: ICommandSuggestionProvider, val name: String? = null) {
}