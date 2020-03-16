package eu.thesimplecloud.launcher.console.command

import com.google.gson.GsonBuilder
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.exception.CommandRegistrationException
import eu.thesimplecloud.launcher.invoker.MethodInvokeHelper
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.parser.string.StringParser
import eu.thesimplecloud.api.utils.getEnumValues
import eu.thesimplecloud.launcher.console.ConsoleSender
import eu.thesimplecloud.launcher.event.command.CommandExecuteEvent
import eu.thesimplecloud.launcher.event.command.CommandRegisteredEvent
import eu.thesimplecloud.launcher.extension.sendMessage
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
        val readLine = if (readLine.trim().equals("cloud", true)) "cloud help" else readLine.trim()

        val covertPathFunction: (CommandData) -> String = { if (commandSender is ConsoleSender) it.path else it.getPathWithCloudPrefixIfRequired() }
        val matchingCommandData = getMatchingCommandData(readLine)
        if (matchingCommandData == null) {
            val list = getAvailableArgsMatchingCommandData(readLine)
            if (commandSender is ConsoleSender) {
                list.forEach { commandSender.sendMessage(">> ${covertPathFunction(it)} (${it.commandDescription})") }
            } else {
                list.filter { it.commandType != CommandType.CONSOLE }.forEach { commandSender.sendMessage("§8>> §7${covertPathFunction(it)}") }
            }
            if (list.isEmpty()) {
                Launcher.instance.logger.warning("This command could not be found! Type \"help\" for help.")
                return
            }

            return
        }
        if (matchingCommandData.commandType == CommandType.CONSOLE && commandSender !is ConsoleSender) {
            commandSender.sendMessage("commandmanager.onlyconsole", "This command can only be executed via the console.")
            return
        }

        val event = CommandExecuteEvent(commandSender, matchingCommandData)
        getCloudAPI()?.getEventManager()?.call(event)
        if (event.isCancelled())
            return

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
                StringParser().parseToObject(parameterValue, parameterData.type)
            } catch (e: Exception) {
                commandSender.sendMessage("§cCan't parse parameter at index $indexOfParameter(\"$parameterValue\") to class ${parameterData.type.simpleName}")
                return
            }

            if (obj == null) {
                commandSender.sendMessage("§cCan't parse parameter at index $indexOfParameter(\"$parameterValue\") to class ${parameterData.type.simpleName}")
                if (parameterData.type.isEnum) {
                    val clazz = parameterData.type as Class<out Enum<*>>
                    val enumValues = clazz.getEnumValues()
                    commandSender.sendMessage("Allowed are: " + enumValues.joinToString(", "))
                }
                return
            }
            list.add(obj)
        }
        MethodInvokeHelper.invoke(matchingCommandData.method, matchingCommandData.source, list.toArray())
    }


    fun getMatchingCommandData(message: String): CommandData? {
        val messageArray = message.split(" ")
        val commandDataList = getCommandDataByArgumentLength(messageArray.size)
        return commandDataList.firstOrNull { commandData ->
            val path = commandData.getPathWithCloudPrefixIfRequired().trim()
            val pathArray = path.split(" ")
            pathArray.withIndex().all { isParamater(it.value) || it.value.toLowerCase() == messageArray[it.index].toLowerCase() }
        }
    }

    fun getAvailableArgsMatchingCommandData(message: String): List<CommandData> {
        val messageArray = message.split(" ")
        val dataList = getCommandDataByMinimumArgumentLength(messageArray.size)
        return dataList.filter { commandData ->
            val path = commandData.getPathWithCloudPrefixIfRequired()
            Launcher.instance.logger.console("$message : $path")
            val pathArray = path.split(" ")
            messageArray.withIndex().all {
                val pathValue = pathArray[it.index]
                isParamater(pathValue) || it.value.toLowerCase() == pathValue.toLowerCase()
            }
        }
    }

    fun isParamater(s: String) = s.startsWith("<") && s.endsWith(">")

    fun getCommandDataByMinimumArgumentLength(length: Int) = this.commands.filter { it.getPathWithCloudPrefixIfRequired().split(" ").size >= length }

    fun getCommandDataByArgumentLength(length: Int) = this.commands.filter { it.getPathWithCloudPrefixIfRequired().trim().split(" ").size == length }

    fun registerAllCommands(cloudModule: ICloudModule, vararg packages: String) {
        packages.forEach { pack ->
            val reflection = Reflections(pack)
            reflection.getSubTypesOf(ICommandHandler::class.java).forEach {
                try {
                    registerCommand(cloudModule, it.getDeclaredConstructor().newInstance())
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        }

        val size = commands.size
        Launcher.instance.logger.success("Loaded $size command" + (if (size == 1) "" else "s"))

    }

    fun registerCommand(cloudModule: ICloudModule, command: ICommandHandler) {
        val commandClass = command::class.java
        val classAnnotation = commandClass.getAnnotation(Command::class.java)
        classAnnotation ?: throw NullPointerException()

        try {
            for (method in commandClass.declaredMethods) {
                val commandSubPath = method.getAnnotation(CommandSubPath::class.java)
                commandSubPath ?: continue

                val commandData = CommandData(cloudModule, classAnnotation.name + " " + commandSubPath.path, commandSubPath.description, command, method, classAnnotation.commandType)
                for (parameter in method.parameters) {
                    val commandArgument = parameter.getAnnotation(CommandArgument::class.java)
                    if (commandArgument == null) {
                        if (!allowedTypesWithoutCommandArgument.contains(parameter.type) || !allowedTypesWithoutCommandArgument.any { it.isAssignableFrom(parameter.type) }) {
                            throw CommandRegistrationException("Forbidden parameter type without CommandArgument annotation.")
                        }
                    }
                    commandData.parameterDataList.add(CommandParameterData(parameter.type, commandArgument?.name))
                }
                commands.add(commandData)
                getCloudAPI()?.getEventManager()?.call(CommandRegisteredEvent(commandData))
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun getAllIngameCommandPrefixes(): Collection<String> = this.commands.filter { it.commandType == CommandType.INGAME }.map { it.path.split(" ")[0] }.toSet().union(listOf("cloud"))

    private fun getCloudAPI(): CloudAPI? {
        return try {
            CloudAPI.instance
        } catch (ex: Exception) {
            return null
        }
    }

}