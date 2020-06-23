/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.launcher.extension

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.ConsoleSender
import eu.thesimplecloud.launcher.startup.Launcher

    /**
     * Sends a message by a property to this player
     * All '&' will be replaced to '§'
     */
    fun ICommandSender.sendMessage(property: String, vararg messages: String) {
        sendMessageToSender(this, false, property, *messages)
    }

    fun ICommandSender.sendMessage(isSetup: Boolean, property: String, vararg messages: String) {
        sendMessageToSender(this, isSetup, property, *messages)
    }

    fun sendMessageToSender(sender: ICommandSender, isSetup: Boolean, property: String, vararg messages: String) {
        val fullRawMessageStringBuilder = StringBuilder()

        if (messages.isEmpty()) {
            sender.sendMessage(property)
            return
        }
        val replacements = HashMap<String, String>()
        for (i in messages.indices step 2) {
            val rawMessage = messages[i]
            fullRawMessageStringBuilder.append(rawMessage)
            if (!rawMessage.contains("%")) continue
            if (!(messages.size > i + 1)) {
                throw IllegalArgumentException("Found % but there was no following string")
            }
            val nextString = messages[i + 1]
            val percentPart = getPercentPart(rawMessage)
            replacements[percentPart] = nextString
        }
        var builtString = Launcher.instance.languageManager.getMessage(property, fullRawMessageStringBuilder.toString())

        replacements.forEach {
            builtString = builtString.replace(it.key, it.value)
        }

        val replacedMessage = builtString.replace("&", "§")
        if (sender is ConsoleSender) {
            sender.sendMessage(replacedMessage, isSetup)
            return
        }
        sender.sendMessage(replacedMessage)
    }

    fun getPercentPart(rawMessage: String) = "%" + rawMessage.split("%")[1].split("%")[0] + "%"