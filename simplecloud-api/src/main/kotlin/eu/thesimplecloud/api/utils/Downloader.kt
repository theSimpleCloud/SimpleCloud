package eu.thesimplecloud.api.utils

import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class Downloader {

    @Throws(IOException::class)
    fun userAgentDownload(url: String, file: File) {
        file.parentFile?.mkdirs()
        val urlConnection = URL(url).openConnection()
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0")
        urlConnection.connect()
        Files.copy(urlConnection.getInputStream(), Paths.get(file.absolutePath), StandardCopyOption.REPLACE_EXISTING)
    }

}