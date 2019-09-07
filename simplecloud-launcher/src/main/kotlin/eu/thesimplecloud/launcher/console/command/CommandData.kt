package eu.thesimplecloud.launcher.console.command

import java.lang.reflect.Method

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 30.08.2019
 * Time: 20:10
 */
class CommandData(
        val path: String,
        val commandDescription: String,
        val source: ICommandHandler,
        val method: Method,
        val parameterDataList: MutableList<CommandParameterData> = ArrayList()
) {

    fun getIndexOfParameter(parameterName: String): Int {
        return path.split(" ").indexOf("<$parameterName>")
    }

}