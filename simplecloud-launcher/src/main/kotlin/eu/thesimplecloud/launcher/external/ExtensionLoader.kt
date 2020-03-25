package eu.thesimplecloud.launcher.external

import java.io.File
import java.lang.reflect.InvocationTargetException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLClassLoader

class ExtensionLoader<C> {

    fun loadClassInstance(jar: File, classpath: String, parentClass: Class<C>): C {
        val newClass = loadClass(jar, classpath, parentClass)
        // Apparently its bad to use Class.newInstance, so we use
        // newClass.getConstructor() instead
        val constructor = newClass.getConstructor()
        return constructor.newInstance()

    }

    fun loadClass(jar: File, classpath: String, parentClass: Class<C>): Class<out C> {
        val urlClassLoader = URLClassLoader(arrayOf(jar.toURI().toURL()), javaClass.classLoader)
        val clazz = Class.forName(classpath, true, urlClassLoader)
        return clazz.asSubclass<C>(parentClass)
    }

}