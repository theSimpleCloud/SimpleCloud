package eu.thesimplecloud.base.manager.commands.provider

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.utils.getAllFieldsFromClassAndSubClasses
import eu.thesimplecloud.launcher.console.command.provider.ICommandSuggestionProvider

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.04.2020
 * Time: 18:39
 */
class EditGroupParameterCommandSuggestionProvider: ICommandSuggestionProvider {

    override fun getSuggestions(sender: ICommandSender, fullCommand: String, lastArgument: String): List<String> {
        val fullCommandArray = fullCommand.split(" ")
        val groupName = fullCommandArray[fullCommandArray.lastIndex - 1]
        val group = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(groupName)
        group ?: return emptyList()
        val allFields = group::class.java.getAllFieldsFromClassAndSubClasses().filter { !Collection::class.java.isAssignableFrom(it.type) }
        return allFields.filterNot { it.name == "name" ||
                it.name == "host" ||
                it.name == "authenticated" ||
                it.name == "usedMemory" ||
                it.name == "templatesReceived" ||
                it.name == "currentlyStartingServices" }.map { it.name }
    }

}