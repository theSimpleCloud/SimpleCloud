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

package eu.thesimplecloud.api.utils

import java.io.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.streams.toList


class FileEditor() {

    var lines: MutableList<String> = ArrayList<String>()
        private set

    constructor(lines: List<String>) : this() {
        this.lines = ArrayList(lines)
    }

    constructor(stream: InputStream) : this() {
        lines = ArrayList(BufferedReader(InputStreamReader(stream,
                StandardCharsets.UTF_8)).lines().toList())
    }

    constructor(file: File) : this() {
        if (!file.exists()) {
            val dir = File(file, "..")
            println()
            dir.mkdirs()
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        lines = ArrayList()
        readFile(file)
    }

    operator fun get(name: String): String? {
        for (s in lines) {
            val array = s.split("=".toRegex()).toTypedArray()
            if (array[0].equals(name, ignoreCase = true)) {
                return array[1]
            }
        }
        return null
    }

    fun write(s: String) {
        lines.add(s)
    }

    fun getLine(i: Int): String? {
        return if (i > lines.size - 1) null else lines[i]
    }

    operator fun set(name: String, value: String) {
        var line = -1
        for (i in lines.indices) {
            val s = lines[i]
            val array = s.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (array[0].equals(name, ignoreCase = true)) {
                line = i
            }
        }
        if (line != -1) {
            lines.removeAt(line)
        }
        lines.add("$name=$value")
    }

    @Throws(IOException::class)
    fun save(file: File) {
        var writer: BufferedWriter? = null

        writer = BufferedWriter(FileWriter(file))
        for (line in lines) {
            writer.write(line)
            writer.newLine()
        }

        writer.close()
    }

    private fun readFile(file: File) {
        this.lines = ArrayList(file.readLines(Charset.defaultCharset()))
    }

    fun replaceLine(line: String, replace: String) {
        for (i in lines.indices) {
            val s = lines[i]
            if (s.equals(line, ignoreCase = true)) {
                lines.removeAt(i)
                lines[i] = replace
                return
            }

        }
    }

    fun replaceInAllLines(old: String, new: String) {
        lines = ArrayList(lines.map { it.replace(old, new) })
    }

}