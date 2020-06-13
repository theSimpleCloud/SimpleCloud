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

package eu.thesimplecloud.api.language

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.jsonlib.JsonLib
import java.io.File

class LanguageManager(var language: String) {

    private lateinit var languageFile: LanguageFile
    private val file = File(DirectoryPaths.paths.languagesPath, "$language.json")
    private var fileExistBeforeLoad = false

    fun loadFile() {
        fileExistBeforeLoad = file.exists()
        if (!file.exists()) {
            languageFile = LanguageFile()

            file.parentFile.mkdirs()
            JsonLib.fromObject(languageFile).saveAsFile(file)
        } else {
            val languageFile = JsonLib.fromJsonFile(file)?.getObjectOrNull(LanguageFile::class.java)
            if (languageFile != null) {
                this.languageFile = languageFile
            }
        }
    }

    fun getMessage(property: String, fallbackMessage: String): String {
        val fileMessage = languageFile.messages[property]
        if (fileMessage == null) {
            addNewProperty(property, fallbackMessage)
            return fallbackMessage
        }

        return fileMessage
    }

    private fun addNewProperty(property: String, message: String) {
        val fileMessage = languageFile.messages[property]
        if (fileMessage == null) {
            languageFile.messages[property] = message
            JsonLib.fromObject(languageFile).saveAsFile(file)
        }
    }

    fun fileExistBeforeLoad() = this.fileExistBeforeLoad

}