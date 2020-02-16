package eu.thesimplecloud.api.external

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import sun.misc.URLClassPath
import java.io.File
import java.io.InputStream
import java.lang.reflect.Field
import java.net.MalformedURLException
import java.net.URL
import java.net.URLClassLoader
import java.util.*


class ResourceFinder {


    companion object {

        @Throws(MalformedURLException::class)
        fun findResource(file: File, pathToResource: String): InputStream? {
            val newClassLoader = URLClassLoader(arrayOf(file.toURI().toURL()))
            println(newClassLoader.urLs.joinToString())
            val resourceAsStream = newClassLoader.getResourceAsStream(pathToResource)
            if (resourceAsStream != null) {
                //println(JsonData.fromInputStream(resourceAsStream))
            }
            return resourceAsStream
        }

        fun addToClassLoader(url: URL, urlClassLoader: URLClassLoader = getSystemClassLoader()) {
            val method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
            method.isAccessible = true
            if (!urlClassLoader.urLs.contains(url))
                method.invoke(urlClassLoader, url)
        }

        fun addToClassLoader(file: File, urlClassLoader: URLClassLoader = getSystemClassLoader()) {
            addToClassLoader(file.toURI().toURL(), urlClassLoader)
        }

        fun removeFromClassLoader(file: File, urlClassLoader: URLClassLoader = getSystemClassLoader()) {
            val url = file.toURI().toURL()
            val urlClass: Class<*> = URLClassLoader::class.java
            val ucpField: Field = urlClass.getDeclaredField("ucp")
            ucpField.isAccessible = true
            val ucp: URLClassPath = ucpField.get(urlClassLoader) as URLClassPath
            val ucpClass: Class<*> = URLClassPath::class.java
            val urlsField: Field = ucpClass.getDeclaredField("urls")
            urlsField.isAccessible = true
            val urls = urlsField.get(ucp) as Stack<*>
            urls.remove(url)
        }

        fun getSystemClassLoader() = ClassLoader.getSystemClassLoader() as URLClassLoader

    }
}