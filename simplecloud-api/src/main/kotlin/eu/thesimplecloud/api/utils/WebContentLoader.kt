package eu.thesimplecloud.api.utils

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class WebContentLoader {

    fun loadContent(urlstring: String): String? {
        val url = URL(urlstring)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()
        val responseCode = connection.responseCode
        if (responseCode != 200) {
            connection.disconnect()
            return null
        }
        val reader = BufferedReader(InputStreamReader(connection.inputStream))

        // write the output to stdout
        var line = ""
        reader.lines().forEach { line += it }
        reader.close()
        connection.disconnect()
        return line
    }

}