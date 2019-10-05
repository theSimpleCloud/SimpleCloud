package eu.thesimplecloud.launcher.external

import java.io.File
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.net.URLClassLoader

class ResourceFinder {

    @Throws(MalformedURLException::class)
     fun findResource(file: File, pathToResource: String): InputStream? {
        return URLClassLoader(arrayOf(file.toURI().toURL())).getResourceAsStream(pathToResource)
    }

}