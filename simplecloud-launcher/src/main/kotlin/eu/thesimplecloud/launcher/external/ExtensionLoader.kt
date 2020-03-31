package eu.thesimplecloud.launcher.external

import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File
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
        val urlClassLoader = URLClassLoader(arrayOf(jar.toURI().toURL()), Launcher.instance.currentClassLoader)
        val clazz = Class.forName(classpath, true, urlClassLoader)
        return clazz.asSubclass<C>(parentClass)
    }

}