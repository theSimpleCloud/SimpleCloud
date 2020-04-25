package eu.thesimplecloud.launcher.console

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.launcher.startup.Launcher

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.09.2019
 * Time: 14:37
 */
class ConsoleSender : ICommandSender {


    override fun sendMessage(message: String): ICommunicationPromise<Unit> {
        if (message.startsWith("§c") || message.startsWith("&c")) {
            Launcher.instance.logger.warning(filerColorCodes(message))
        } else {
            Launcher.instance.logger.console(filerColorCodes(message))
        }
        return CommunicationPromise.of(Unit)
    }

    override fun hasPermission(permission: String): ICommunicationPromise<Boolean> {
        return CommunicationPromise.of(true)
    }

    private fun filerColorCodes(message: String): String {
        val builder = StringBuilder()
        val charArray = message.toCharArray()
        var lastChar = '0'
        for (i in charArray.indices) {
            val char = charArray[i]
            val isCharToRemove = char == '§' || lastChar == '§'
            lastChar = char
            if (isCharToRemove) {
                continue
            }
            builder.append(char)
        }
        return builder.toString()
    }
}