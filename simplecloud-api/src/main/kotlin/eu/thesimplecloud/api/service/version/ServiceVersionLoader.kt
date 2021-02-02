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

package eu.thesimplecloud.api.service.version

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.utils.WebContentLoader
import eu.thesimplecloud.jsonlib.JsonLib
import java.io.File
import java.io.FileNotFoundException

/**
 * Created by IntelliJ IDEA.
 * Date: 14.06.2020
 * Time: 19:07
 * @author Frederick Baier
 *
 * Added custom version support - NenoxAG (nenox@nenox.dev)
 */
object ServiceVersionLoader {

    private val webVersionFile = File(DirectoryPaths.paths.storagePath + "mc-versions.json")
    private val localVersionsFile = File(DirectoryPaths.paths.storagePath + "local-mc-versions.json")

    fun loadVersions(): List<ServiceVersion> {
        val webVersions = loadFromWeb()
        val localVersions = loadLocalVersions()

        val combinedVersions = ArrayList<ServiceVersion>()

        combinedVersions.addAll(webVersions)
        combinedVersions.addAll(localVersions)

        return combinedVersions
    }

    private fun loadLocalVersions(): List<ServiceVersion> {
        return loadLocalVersionsFromFile()
    }

    private fun loadFromWeb(): List<ServiceVersion> {
        val contentString = WebContentLoader().loadContent("https://api.thesimplecloud.eu/versions")
        return if (contentString == null) {
            loadWebVersionsFromFile()
        } else {
            processWebContent(contentString)
        }
    }

    private fun loadWebVersionsFromFile(): List<ServiceVersion> {
        if (!webVersionFile.exists()) throw FileNotFoundException("File ${webVersionFile.absolutePath} does not exist and the web server to load the service versions from is not available.")
        return JsonLib.fromJsonFile(webVersionFile)!!.getObject(Array<ServiceVersion>::class.java).toList()
    }

    private fun loadLocalVersionsFromFile(): List<ServiceVersion> {
        if (!localVersionsFile.exists()) {
            val jsonLib = JsonLib.fromJsonString("[{\"name\": \"EXAMPLE\", \"serviceAPIType\": \"EXAMPLE\", \"downloadURL\": \"noDownload\"}]")
            jsonLib.saveAsFile(localVersionsFile)
        }

        return JsonLib.fromJsonFile(localVersionsFile)!!.getObject(Array<ServiceVersion>::class.java).toList()
    }

    private fun processWebContent(contentString: String): List<ServiceVersion> {
        val jsonLib = JsonLib.fromJsonString(contentString)
        jsonLib.saveAsFile(webVersionFile)
        return jsonLib.getObject(Array<ServiceVersion>::class.java).toList()
    }

}