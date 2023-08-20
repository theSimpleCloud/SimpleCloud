/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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

package eu.thesimplecloud.base.manager.commands.provider

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.utils.getAllFieldsFromClassAndSubClasses
import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperInfo
import eu.thesimplecloud.launcher.console.command.provider.ICommandSuggestionProvider

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.04.2020
 * Time: 18:39
 */
class EditWrapperParameterCommandSuggestionProvider : ICommandSuggestionProvider {

    override fun getSuggestions(sender: ICommandSender, fullCommand: String, lastArgument: String): List<String> {
        val allFields = DefaultWrapperInfo::class.java.getAllFieldsFromClassAndSubClasses()
            .filter { !Collection::class.java.isAssignableFrom(it.type) }
        return allFields.filterNot {
            it.name == "name" ||
                    it.name == "host" ||
                    it.name == "authenticated" ||
                    it.name == "usedMemory" ||
                    it.name == "templatesReceived" ||
                    it.name == "cpuUsage" ||
                    it.name == "wrapperUpdater" ||
                    it.name == "currentlyStartingServices"
        }.map { it.name }
    }

}