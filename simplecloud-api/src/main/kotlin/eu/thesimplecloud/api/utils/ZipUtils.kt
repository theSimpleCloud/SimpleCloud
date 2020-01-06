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

}

