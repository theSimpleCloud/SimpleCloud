package eu.thesimplecloud.base.manager.commands.provider

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.utils.getAllFieldsFromClassAndSubClasses
import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperInfo
import eu.thesimplecloud.launcher.console.command.provider.ICommandSuggestionProvider

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.04.2020
 * Time: 18:39
 */
class EditWrapperParameterCommandSuggestionProvider : ICommandSuggestionProvider {

    override fun getSuggestions(sender: ICommandSender, fullCommand: String, lastArgument: String): List<String> {
        val allFields = DefaultWrapperInfo::class.java.getAllFieldsFromClassAndSubClasses().filter { !Collection::class.java.isAssignableFrom(it.type) }
        return allFields.filterNot { it.name == "name" ||
                it.name == "host" ||
                it.name == "authenticated" ||
                it.name == "usedMemory" ||
                it.name == "templatesReceived" ||
                it.name == "currentlyStartingServices" }.map { it.name }
    }

}