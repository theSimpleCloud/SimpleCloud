package eu.thesimplecloud.launcher.console.command

import eu.thesimplecloud.api.external.ICloudModule
import java.lang.reflect.Method

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 30.08.2019
 * Time: 20:10
 */
class CommandData(
        val cloudModule: ICloudModule,
        val path: String,
        val commandDescription: String,
        val source: ICommandHandler,
        val method: Method,
        val commandType: CommandType,
        val permission: String,
        val aliases: Array<String>,
        val isLegacy: Boolean,
        val parameterDataList: MutableList<CommandParameterData> = ArrayList()
) {

    fun getParameterDataByName(name: String) = this.parameterDataList.firstOrNull { it.name == name }

    fun getParameterDataByNameWithBraces(name: String) = getParameterDataByName(name.drop(1).dropLast(1))

    fun getPathWithCloudPrefixIfRequired() = getPathWithCloudPrefixIfRequired(this.path)

    fun getPathWithCloudPrefixIfRequired(path: String) = (if (commandType == CommandType.INGAME) "" else "cloud ") + path

    fun getAllPathsWithAliases(): Collection<String> {
        val path = path.split(" ").drop(1).joinToString(" ")
        return aliases.map { getPathWithCloudPrefixIfRequired("$it $path") }.union(listOf(getPathWithCloudPrefixIfRequired()))
    }

    fun getIndexOfParameter(parameterName: String): Int {
        return getPathWithCloudPrefixIfRequired().split(" ").indexOf("<$parameterName>")
    }

    fun hasCloudPrefix() = this.commandType != CommandType.INGAME

}