package eu.thesimplecloud.launcher.extension

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.startup.Launcher
import java.lang.IllegalArgumentException

    /**
     * Sends a message by a property to this player
     * All '&' will be replaced to '§'
     */
    fun ICommandSender.sendMessage(property: String, vararg messages: String) {
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
        var builtString = Launcher.instance.languageManager.getMessage(property, fullRawMessageStringBuilder.toString())

        replacements.forEach{
            builtString = builtString.replace(it.key, it.value)
        }
        sendMessage(builtString.replace("&", "§"))
    }

    fun getPercentPart(rawMessage: String) = "%" + rawMessage.split("%")[1].split("%")[0] + "%"

