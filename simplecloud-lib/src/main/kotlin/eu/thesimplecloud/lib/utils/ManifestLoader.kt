package eu.thesimplecloud.lib.utils

import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
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