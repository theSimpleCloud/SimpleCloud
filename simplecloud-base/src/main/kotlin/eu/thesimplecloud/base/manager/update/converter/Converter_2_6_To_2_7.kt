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

package eu.thesimplecloud.base.manager.update.converter

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.base.manager.update.converter.IVersionConverter
import eu.thesimplecloud.jsonlib.JsonLib
import java.io.File

/**
 * Date: 18.06.22
 * Time: 16:44
 * @author Frederick Baier
 *
 */
class Converter_2_6_To_2_7 : IVersionConverter {

    override fun getTargetMinorVersion(): Int {
        return 7
    }

    override fun convert() {
        convertGroupsInDirectory(File(DirectoryPaths.paths.proxyGroupsPath))
        convertGroupsInDirectory(File(DirectoryPaths.paths.lobbyGroupsPath))
        convertGroupsInDirectory(File(DirectoryPaths.paths.serverGroupsPath))

        val modulesPath = DirectoryPaths.paths.modulesPath
        File(modulesPath, "support").deleteRecursively()
        File(modulesPath).listFiles()!!
            .filter { !it.isDirectory }
            .filter { it.name.lowercase().contains("simplecloud-support") }
            .forEach { it.deleteRecursively() }
    }

    private fun convertGroupsInDirectory(dir: File) {
        dir.listFiles()?.forEach {
            convertSingleGroupByFile(it)
        }
    }

    private fun convertSingleGroupByFile(file: File) {
        val groupJsonLib = JsonLib.fromJsonFile(file) ?: return

        val maxMemory = groupJsonLib.jsonElement.asJsonObject.get("maxMemory").asInt

        if (!groupJsonLib.jsonElement.asJsonObject.has("minimumMemory")) {
            groupJsonLib.jsonElement.asJsonObject.addProperty("minimumMemory", maxMemory / 2)
        }

        groupJsonLib.saveAsFile(file)
    }

}