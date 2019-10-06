package eu.thesimplecloud.lib.language

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import java.io.File

class LanguageManager(var language: String) {

    private lateinit var languageFile: LanguageFile
    private val file = File(DirectoryPaths.paths.languagesPath, "$language.json")

    fun loadFile() {
        if (!file.exists()) {
            languageFile = LanguageFile()

            file.parentFile.mkdirs()
            JsonData.fromObject(languageFile).saveAsFile(file)
        } else {
            val languageFile = JsonData.fromJsonFile(file).getObject(LanguageFile::class.java)
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
            languageFile.messages.put(property, message)
            JsonData.fromObject(languageFile).saveAsFile(file)
        }
    }

    fun doesFileExist() = file.exists()

}