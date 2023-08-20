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

package eu.thesimplecloud.base.manager.startup

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.jsonlib.JsonLib
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.name
import kotlin.io.path.pathString

class ProfileFile {

    private val file = File("profile.sc")

    fun create() {
        val pathStream = Files.walk(Paths.get(""))
        val jsonLib = JsonLib.empty()
        pathStream
            .filter(Files::isRegularFile)
            .filter {
                it.name.endsWith(".json")
                        && !it.name.contains("database")
                        && !it.pathString.contains(DirectoryPaths.paths.languagesPath)
            }
            .forEach {
                val pathName = it.pathString.replace(".json", "")
                val file = File(it.toUri())
                val jsonFile = JsonLib.fromJsonFile(file) ?: return@forEach
                jsonLib.append(pathName, jsonFile.jsonElement)
            }

        pathStream.close()
        jsonLib.saveAsFile(file)
    }

}