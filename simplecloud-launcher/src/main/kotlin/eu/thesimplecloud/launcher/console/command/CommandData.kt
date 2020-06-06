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
        val aliases: Array<String>,
        val isLegacy: Boolean,
        val parameterDataList: MutableList<CommandParameterData> = ArrayList()
) {

    fun getParameterDataByName(name: String) = this.parameterDataList.firstOrNull { it.name == name }

    fun getParameterDataByNameWithBraces(name: String) = getParameterDataByName(name.drop(1).dropLast(1))

    fun getPathWithCloudPrefixIfRequired() = getPathWithCloudPrefixIfRequired(this.path)

    fun getPathWithCloudPrefixIfRequired(path: String) = (if (commandType == CommandType.INGAME) "" else "cloud ") + path

    fun getAllPathsWithAliases(): Collection<String> {
        val path = path.split(" ").drop(1).joinToString(" ")
        return aliases.map { getPathWithCloudPrefixIfRequired("$it $path") }.union(listOf(getPathWithCloudPrefixIfRequired()))
    }

    fun getIndexOfParameter(parameterName: String): Int {
        return getPathWithCloudPrefixIfRequired().split(" ").indexOf("<$parameterName>")
    }

    fun hasCloudPrefix() = this.commandType != CommandType.INGAME

}