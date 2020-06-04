/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.api.external

import sun.misc.URLClassPath
import java.io.File
import java.io.FileNotFoundException
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
            return newClassLoader.getResourceAsStream(pathToResource)
        }

        fun addToClassLoader(url: URL, urlClassLoader: URLClassLoader = getThreadClassLoader()) {
            val method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
            method.isAccessible = true
            if (!urlClassLoader.urLs.contains(url))
                method.invoke(urlClassLoader, url)
        }

        fun addToClassLoader(file: File, urlClassLoader: URLClassLoader = getThreadClassLoader()) {
            addToClassLoader(file.toURI().toURL(), urlClassLoader)
        }

        fun removeFromClassLoader(file: File, urlClassLoader: URLClassLoader = getThreadClassLoader()) {
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

        private fun getThreadClassLoader() = Thread.currentThread().contextClassLoader as URLClassLoader



        @Throws(FileNotFoundException::class)
        fun getFileFromClass(clazz: Class<*>): File {
            val resource = clazz.getResource("/${clazz.name.replace('.', '/')}.class").toString()
            if (!resource.contains("!")) throw FileNotFoundException("Unable to find file by class ${clazz.name}")
            val path = try {
                val split = resource.split(":")
                split.drop(2).joinToString(":").split("!")[0]
            } catch (ex: Exception) {
                throw FileNotFoundException("Unable to find file by class ${clazz.name}")
            }
            return File(path)
        }

        fun createClassLoaderByFiles(vararg files: File): URLClassLoader {
            return URLClassLoader(files.map { it.toURI().toURL() }.toTypedArray())
        }

    }
}