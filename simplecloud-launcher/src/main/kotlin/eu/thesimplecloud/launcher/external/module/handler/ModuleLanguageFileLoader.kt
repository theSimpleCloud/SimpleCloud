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

package eu.thesimplecloud.launcher.external.module.handler

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.language.LoadedLanguageFile
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.launcher.exception.module.ModuleLoadException
import eu.thesimplecloud.launcher.language.LanguageFileLoader
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Created by IntelliJ IDEA.
 * Date: 29.11.2020
 * Time: 17:27
 * @author Frederick Baier
 */
class ModuleLanguageFileLoader(
    private val currentLanguage: String,
    private val moduleFile: File,
    private val cloudModule: ICloudModule
) {

    fun registerLanguageFileIfExist() {
        val languageFile = loadLanguageFile(moduleFile)
        languageFile?.let {
            CloudAPI.instance.getLanguageManager().registerLanguageFile(cloudModule, languageFile)
        }
    }

    private fun loadLanguageFile(file: File): LoadedLanguageFile? {
        val loadedLanguageFile = loadLanguageFile(file, this.currentLanguage)
        return loadedLanguageFile ?: loadFallbackLanguageFile(file)
    }

    private fun loadFallbackLanguageFile(file: File): LoadedLanguageFile? {
        return loadLanguageFile(file, LanguageFileLoader.FALLBACK_LANGUAGE)
    }

    private fun loadLanguageFile(file: File, language: String): LoadedLanguageFile? {
        val map: Map<String, String>? =
            runCatching { loadJsonFileInJar<HashMap<String, String>>(file, "languages/${language}.json") }.getOrNull()
        return map?.let { LanguageFileLoader().buildFileFromMap(it) }
    }

    private inline fun <reified T : Any> loadJsonFileInJar(file: File, path: String): T {
        require(file.exists()) { "Specified file to load $path from does not exist: ${file.path}" }
        try {
            val jar = JarFile(file)
            val entry: JarEntry = jar.getJarEntry(path)
                ?: throw ModuleLoadException("${file.path}: No '$path' found.")
            val fileStream = jar.getInputStream(entry)
            val jsonLib = JsonLib.fromInputStream(fileStream)
            jar.close()
            return jsonLib.getObjectOrNull(T::class.java)
                ?: throw ModuleLoadException("${file.path}: Invalid '$path'.")
        } catch (ex: Exception) {
            throw ModuleLoadException(file.path, ex)
        }
    }

}