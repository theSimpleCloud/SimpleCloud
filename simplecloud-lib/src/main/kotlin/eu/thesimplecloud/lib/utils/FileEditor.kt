package eu.thesimplecloud.lib.utils

import java.io.*
import java.util.ArrayList
import jdk.nashorn.internal.runtime.ScriptingFunctions.readLine
import sun.misc.IOUtils
import java.io.FileReader
import java.io.BufferedReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
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