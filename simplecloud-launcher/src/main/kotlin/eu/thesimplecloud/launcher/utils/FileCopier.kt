/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
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

package eu.thesimplecloud.launcher.utils

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.launcher.startup.Launcher
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.jar.JarFile

class FileCopier {

    companion object {

        val classLoader = Launcher.instance.getNewClassLoaderWithLauncherAndBase()

        /**
         * Copies a file outside this jar
         */
        fun copyFileOutOfJar(fileDestination: File, filePathToCopy: String) {
            val stream = FileCopier::class.java.getResourceAsStream(filePathToCopy)
                ?: loadFromBaseJar(filePathToCopy)

            val parent = fileDestination.parentFile
            parent?.mkdirs()
            if (File(filePathToCopy).exists()) {
                return
            }
            try {
                fileDestination.createNewFile()
                FileUtils.copyInputStreamToFile(stream, fileDestination)
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

        }

        private fun loadFromBaseJar(filePathToCopy: String): InputStream {
            val jarFile = JarFile(DirectoryPaths.paths.storagePath + "base.jar")
            //drop first "/"
            val jarEntry = jarFile.getJarEntry(filePathToCopy.drop(1))
            return jarFile.getInputStream(jarEntry)
        }
    }

}