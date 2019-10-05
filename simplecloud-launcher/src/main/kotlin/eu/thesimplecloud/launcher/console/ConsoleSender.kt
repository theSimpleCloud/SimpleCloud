package eu.thesimplecloud.launcher.console

import eu.thesimplecloud.launcher.Launcher
import eu.thesimplecloud.launcher.console.command.ICommandSender
import java.lang.IllegalArgumentException
import kotlin.text.StringBuilder

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.09.2019
 * Time: 14:37
 */
class ConsoleSender : ICommandSender {
    override fun sendMessage(property: String, vararg messages: String) {
        val fullRawMessageStringBuilder = StringBuilder()

        if (messages.isEmpty()) {
            sendMessage(property)
            return
        }
        val replacements = HashMap<String, String>()
        for (i in messages.indices step 2) {
            val rawMessage = messages[i]
            fullRawMessageStringBuilder.append(rawMessage)
            if (!rawMessage.contains("%")) continue
            if (!(messages.size > i + 1)){
                throw IllegalArgumentException("Found % but there was no following string")
            }
            val nextString = messages[i + 1]
            val percentPart = getPercentPart(rawMessage)
            replacements[percentPart] = nextString
        }
        var buildedString = Launcher.instance.languageManager.getMessage(property, fullRawMessageStringBuilder.toString())

        replacements.forEach{
            buildedString = buildedString.replace(it.key, it.value)
        }
        sendMessage(buildedString)
    }

    fun getPercentPart(rawMessage: String) = "%" + rawMessage.split("%")[1].split("%")[0] + "%"

    fun replacePercent(rawMessage: String, replacement: String): String {
        return rawMessage.split("%")[0] + replacement + " "
    }

    override fun sendMessage(message: String) {
        if (message.startsWith("§c") || message.startsWith("&c")) {
            Launcher.instance.logger.warning(message.replace("§c", ""))
        } else {
            Launcher.instance.logger.console(message)
        }
    }
}