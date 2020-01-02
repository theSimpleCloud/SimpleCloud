package eu.thesimplecloud.api.external

import java.io.File
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.net.URLClassLoader

class ResourceFinder {


    companion object {

        @Throws(MalformedURLException::class)
        fun findResource(file: File, pathToResource: String): InputStream? {
            addToClassLoader(file)
            return ClassLoader.getSystemClassLoader().getResourceAsStream(pathToResource)
        }

        fun addToClassLoader(file: File, urlClassLoader: URLClassLoader) {
            val method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
            method.isAccessible = true
            if (!urlClassLoader.urLs.contains(file.toURI().toURL()))
                method.invoke(urlClassLoader, file.toURI().toURL())
        }

        fun getSystemClassLoader() = ClassLoader.getSystemClassLoader() as URLClassLoader

        fun addToClassLoader(file: File) {
            addToClassLoader(file, getSystemClassLoader())
        }

    }
}