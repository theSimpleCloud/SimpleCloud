package eu.thesimplecloud.launcher.console.command

import com.google.gson.GsonBuilder
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.Launcher
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.exception.CommandRegistrationException
import eu.thesimplecloud.launcher.invoker.MethodInvokeHelper
import org.reflections.Reflections
import java.lang.NullPointerException
import kotlin.collections.ArrayList


/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 30.08.2019
 * Time: 17:41
 */
class CommandManager() {

    val GSON = GsonBuilder().serializeNulls().create()

    val commands = ArrayList<CommandData>()
    val allowedTypesWithoutCommandArgument = listOf(ICommandSender::class.java)

    fun handleCommand(readLine: String, commandSender: ICommandSender) {
        val readLine = readLine.trim()
        val matchingCommandData = getMatchingCommandData(readLine)
        if (matchingCommandData == null) {
            val list = getAvailableArgsMatchingCommandData(readLine)

            list.forEach { commandSender.sendMessage(">> ${it.path} (${it.commandDescription})") }
            if (list.isEmpty()) {
                Launcher.instance.logger.warning("This command could not be found! Type \"help\" for help.")
                return
            }

            return
        }
        val list = ArrayList<Any?>()
        val messageArray = readLine.split(" ")
        for (parameterData in matchingCommandData.parameterDataList) {
            if (parameterData.name == null) {
                when (parameterData.type) {
                    ICommandSender::class.java -> list.add(commandSender)
                }
                continue
            }

            val parameterName: String = parameterData.name
            val indexOfParameter = matchingCommandData.getIndexOfParameter(parameterName)
            val parameterValue = messageArray[indexOfParameter]

            val obj = try {
                GSON.fromJson(parameterValue, parameterData.type)
            } catch (e: Exception) {
                commandSender.sendMessage("§cCan't parse parameter at index $indexOfParameter(\"$parameterValue\") to class ${parameterData.type.simpleName}")
                return
            }

            if (obj == null) {
                commandSender.sendMessage("§cCan't parse parameter at index $indexOfParameter(\"$parameterValue\") to class ${parameterData.type.simpleName}")
                if (parameterData.type.isEnum) {
                    val enumValues = getEnumValues(parameterData.type as Class<out Enum<*>>)
                    commandSender.sendMessage("Allowed are: " + enumValues.joinToString(", "))
                }
                return
            }
            list.add(obj)
        }
        MethodInvokeHelper.invoke(matchingCommandData.method, matchingCommandData.source, list.toArray())
    }

    fun getEnumValues(clazz: Class<out Enum<*>>): List<String> {
        val method = clazz.getMethod("values")
        val values = method.invoke(null) as Array<Enum<*>>
        return values.map { it.name }
    }

    fun getMatchingCommandData(message: String): CommandData? {
        val messageArray = message.split(" ")
        val commandDataList = getCommandDataByArgumentLength(messageArray.size)
        return commandDataList.firstOrNull { commandData ->
            val path = commandData.path.trim()
            val pathArray = path.split(" ")
            pathArray.withIndex().all { isParamater(it.value) || it.value.toLowerCase() == messageArray[it.index].toLowerCase() }
        }
    }

    fun getAvailableArgsMatchingCommandData(message: String): List<CommandData> {
        val messageArray = message.split(" ")
        val dataList = getCommandDataByMinimumArgumentLength(messageArray.size)
        return dataList.filter { commandData ->
            val path = commandData.path
            val pathArray = path.split(" ")
            messageArray.withIndex().all {
                val pathValue = pathArray[it.index]
                isParamater(pathValue) || it.value.toLowerCase() == pathValue.toLowerCase()
            }
        }
    }

    fun isParamater(s: String) = s.startsWith("<") && s.endsWith(">")

    fun getCommandDataByMinimumArgumentLength(length: Int) = this.commands.filter { it.path.split(" ").size >= length }

    fun getCommandDataByArgumentLength(length: Int) = this.commands.filter { it.path.trim().split(" ").size == length }

    fun registerAllCommands(vararg packages: String) {
        packages.forEach {
            val reflection = Reflections(it)
            reflection.getSubTypesOf(ICommandHandler::class.java).forEach {
                try {
                    registerCommand(it.getDeclaredConstructor().newInstance())
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        }

        val size = commands.map { it.path.split(" ")[0] }.toSet().size;
        Launcher.instance.logger.success("Loaded $size command" + (if (size == 1) "" else "s"))

    }

    fun registerCommand(command: ICommandHandler) {
        val commandClass = command::class.java
        val classAnnotation = commandClass.getAnnotation(Command::class.java)
        classAnnotation ?: throw NullPointerException()

        try {
            for (method in commandClass.declaredMethods) {
                val commandSubPath = method.getAnnotation(CommandSubPath::class.java)
                commandSubPath ?: continue

                val commandData = CommandData(classAnnotation.name + " " + commandSubPath.path, commandSubPath.description, command, method)
                for (parameter in method.parameters) {
                    val commandArgument = parameter.getAnnotation(CommandArgument::class.java)
                    if (commandArgument == null) {
                        if (!allowedTypesWithoutCommandArgument.contains(parameter.type) || !allowedTypesWithoutCommandArgument.any { it.isAssignableFrom(parameter.type) }) {
                            throw CommandRegistrationException("Unallowed parameter type without CommandArgument annotation.")
                        }
                    }
                    commandData.parameterDataList.add(CommandParameterData(parameter.type, commandArgument?.name))
                }
                commands.add(commandData)
            }
        } catch (e: Exception) {
        }

    }
}