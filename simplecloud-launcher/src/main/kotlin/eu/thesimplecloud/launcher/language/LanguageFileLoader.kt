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

package eu.thesimplecloud.launcher.language

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.language.LanguageProperty
import eu.thesimplecloud.api.language.LoadedLanguageFile
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.launcher.config.LauncherConfig
import eu.thesimplecloud.launcher.utils.FileCopier
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 18.10.2020
 * Time: 15:30
 * @author Frederick Baier
 */
class LanguageFileLoader {

    private val languageDir = File(DirectoryPaths.paths.languagesPath)

    private val allLanguages = listOf("en", "de")

    fun loadFile(launcherConfig: LauncherConfig) {
        if (!languageDir.exists() || languageDir.listFiles().isEmpty()) {
            languageDir.mkdirs()
            copyLanguageFiles()
        }
        val language = launcherConfig.language
        loadLanguage(language)
    }

    private fun copyLanguageFiles() {
        allLanguages.forEach {
            FileCopier.copyFileOutOfJar(getLanguageFileByLanguage(it), "/language/$it.json")
        }
    }

    private fun loadLanguage(language: String) {
        val languageFile = getLanguageFileByLanguage(language)
        if (!languageFile.exists()) {
            if (languageFile != getLanguageFileByLanguage(FALLBACK_LANGUAGE)) {
                loadLanguage(FALLBACK_LANGUAGE)
                return
            }
        }
        CloudAPI.instance.getLanguageManager()
            .registerLanguageFile(CloudAPI.instance.getThisSidesCloudModule(), loadLanguageFile(languageFile))
    }

    fun loadLanguageFile(file: File): LoadedLanguageFile {
        val map = JsonLib.fromJsonFile(file)!!.getObject(HashMap::class.java) as Map<String, String>
        return buildFileFromMap(map)
    }

    fun buildFileFromMap(map: Map<String, String>): LoadedLanguageFile {
        val properties = map.entries.map { LanguageProperty(it.key, it.value) }
        return LoadedLanguageFile(properties)
    }

    private fun getLanguageFileByLanguage(language: String): File {
        return File(languageDir, "$language.json")
    }

    companion object {
        const val FALLBACK_LANGUAGE = "en"

        val isFirstStart = !LanguageFileLoader().getLanguageFileByLanguage(FALLBACK_LANGUAGE).exists()
    }

}