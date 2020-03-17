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
        val parameterDataList: MutableList<CommandParameterData> = ArrayList()
) {

    fun getPathWithCloudPrefixIfRequired() = (if (commandType == CommandType.INGAME) "" else "cloud ") + path

    fun getIndexOfParameter(parameterName: String): Int {
        return getPathWithCloudPrefixIfRequired().split(" ").indexOf("<$parameterName>")
    }

}