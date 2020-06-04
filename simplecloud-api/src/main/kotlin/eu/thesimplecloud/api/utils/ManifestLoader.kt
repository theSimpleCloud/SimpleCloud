/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
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

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.ZipEntry
import java.util.zip.ZipFile


class ManifestLoader {

    companion object {
        fun getMainClass(sourceJARFile: String): String? {
            val fileEditor = FileEditor(readManifest(sourceJARFile))
            fileEditor.replaceInAllLines(": ", "=")
            return fileEditor["Main-Class"]
        }

        @Throws(IOException::class)
        fun readManifest(sourceJARFile: String): List<String> {
            val zipFile = ZipFile(sourceJARFile)
            val entries = zipFile.entries()

            while (entries.hasMoreElements()) {
                val zipEntry = entries.nextElement() as ZipEntry
                if (zipEntry.name == "META-INF/MANIFEST.MF") {
                    return toString(zipFile.getInputStream(zipEntry))
                }
            }

            throw IllegalStateException("Manifest not found")
        }

        @Throws(IOException::class)
        private fun toString(inputStream: InputStream): List<String> {
            return BufferedReader(InputStreamReader(inputStream)).readLines()
        }
    }



}