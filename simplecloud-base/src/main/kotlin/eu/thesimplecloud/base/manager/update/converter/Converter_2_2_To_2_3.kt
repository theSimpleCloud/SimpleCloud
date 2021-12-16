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

package eu.thesimplecloud.base.manager.update.converter

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.utils.ZipUtils
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 29/01/2021
 * Time: 21:15
 * @author Frederick Baier
 */
class Converter_2_2_To_2_3 : IVersionConverter {

    override fun getTargetMinorVersion(): Int {
        return 3
    }

    override fun convert() {
        deleteJarsWithoutGson()
    }

    //Deletes the jar files without gson inside.
    //Because in SimpleCloud 2.2 gson was deleted from downloaded jar files to ensure compatibility with 1.8
    //Since 1.8 is no longer supported gson is no longer linked into the classpath.
    //Therefore, the jars have to contain gson. Otherwise, they won't start.
    //To ensure that gson is inside the jars all jars without gson get deleted and re-downloaded automatically, when they are needed.
    private fun deleteJarsWithoutGson() {
        for (jarFile in getMinecraftJarFiles()) {
            deleteIfFileHasNoGson(jarFile)
        }
    }


    private fun deleteIfFileHasNoGson(file: File) {
        if (!ZipUtils().hasPath(file, "com/google/gson/")) {
            file.delete()
        }
    }

    private fun getMinecraftJarFiles(): List<File> {
        val minecraftJarsPath = DirectoryPaths.paths.minecraftJarsPath
        val minecraftJarsDir = File(minecraftJarsPath)
        if (!minecraftJarsDir.exists()) return emptyList()
        return minecraftJarsDir.listFiles()!!.filter { it.name.endsWith(".jar") }
    }
}