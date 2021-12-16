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

package eu.thesimplecloud.api.utils

import java.io.File
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipFile


class ZipUtils {

    fun deletePath(file: File, path: String) {
        val env = HashMap<String, String>()
        env["create"] = "true"
        env["encoding"] = "UTF-8"

        val zipFile = ZipFile(file)
        val entries = zipFile.entries()
        val uri = URI.create("jar:" + file.toURI().toString())
        val fileSystem = FileSystems.newFileSystem(uri, env)
        while (entries.hasMoreElements()) {
            val nextElement = entries.nextElement()
            if (nextElement.name.startsWith(path) && !nextElement.isDirectory) {
                val pathInZipfile: Path = fileSystem.getPath(nextElement.name)
                //println("About to delete an entry from ZIP File" + pathInZipfile.toUri())
                Files.delete(pathInZipfile)
                //println("File successfully deleted")
            }
        }
        zipFile.close()
        fileSystem.close()

    }

    fun hasPath(file: File, path: String): Boolean {
        val env = HashMap<String, String>()
        env["create"] = "true"
        env["encoding"] = "UTF-8"

        val zipFile = ZipFile(file)
        val entries = zipFile.entries()
        val uri = URI.create("jar:" + file.toURI().toString())
        val fileSystem = FileSystems.newFileSystem(uri, env)
        while (entries.hasMoreElements()) {
            val nextElement = entries.nextElement()
            if (nextElement.name.startsWith(path) && !nextElement.isDirectory) {
                zipFile.close()
                fileSystem.close()
                return true
            }
        }
        zipFile.close()
        fileSystem.close()
        return false
    }

}

