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

package eu.thesimplecloud.api.service.version.loader

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.service.version.ServiceVersion
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.runner.utils.WebContentLoader
import java.io.File
import java.io.FileNotFoundException

/**
 * Created by IntelliJ IDEA.
 * Date: 29/01/2021
 * Time: 21:07
 * @author Frederick Baier
 */
class ServiceVersionWebLoader : IServiceVersionLoader {

    private val file = File(DirectoryPaths.paths.storagePath + "onlineServiceVersions.json")

    override fun loadVersions(): List<ServiceVersion> {
        val version = this::class.java.`package`.implementationVersion.substring(0, 3)
        val contentString = WebContentLoader().loadContent("https://api.thesimplecloud.eu/versions?implementationVersion[\$lte]=$version")
        return if (contentString == null) {
            loadFromFile()
        } else {
            processWebContent(contentString)
        }
    }

    private fun loadFromFile(): List<ServiceVersion> {
        if (!file.exists()) throw FileNotFoundException("File ${file.absolutePath} does not exist and the web server to load the service versions from is not available")
        return JsonLib.fromJsonFile(file)!!.getObject(Array<ServiceVersion>::class.java).toList()
    }

    private fun processWebContent(contentString: String): List<ServiceVersion> {
        val jsonLib = JsonLib.fromJsonString(contentString)
        jsonLib.saveAsFile(file)
        return jsonLib.getObject(Array<ServiceVersion>::class.java).toList()
    }
}