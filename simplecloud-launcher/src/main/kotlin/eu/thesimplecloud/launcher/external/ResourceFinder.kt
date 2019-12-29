package eu.thesimplecloud.launcher.external

import java.io.File
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.net.URLClassLoader

class ResourceFinder {


    companion object {

        @Throws(MalformedURLException::class)
        fun findResource(file: File, pathToResource: String): InputStream? {
            addToClassPath(file)
            return ClassLoader.getSystemClassLoader().getResourceAsStream(pathToResource)
        }


        fun addToClassPath(file: File) {
            val urlClassLoader = ClassLoader.getSystemClassLoader() as URLClassLoader
            val method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
            method.isAccessible = true
            if (!urlClassLoader.urLs.contains(file.toURI().toURL()))
                method.invoke(urlClassLoader, file.toURI().toURL())
        }

    }
}