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

package eu.thesimplecloud.launcher.console

import eu.thesimplecloud.api.CloudAPI
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

    fun sendPropertyInSetup(property: String, vararg placeholderValues: String) {
        if (CloudAPI.isAvailable()) {
            val replacedMessage = CloudAPI.instance.getLanguageManager().getMessage(property, *placeholderValues)
            Launcher.instance.logger.setup(replacedMessage)
            return
        }
        Launcher.instance.logger.setup(property)
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