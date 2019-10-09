package eu.thesimplecloud.launcher.utils

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class WebsiteContentLoader {


    fun loadContent(urlstring: String): String{
        val url = URL(urlstring)
        val reader = BufferedReader(InputStreamReader(url.openStream()))

        // write the output to stdout
        var line = ""
        reader.lines().forEach { line += it }
        reader.close()
        return line
    }

}