package eu.thesimplecloud.lib.utils

import java.io.*
import java.util.ArrayList
import jdk.nashorn.internal.runtime.ScriptingFunctions.readLine
import java.io.FileReader
import java.io.BufferedReader
import java.nio.charset.Charset


class FileEditor(private val file: File) {

    private var lines: MutableList<String>

    init {
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
            val array = s.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
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
    fun save() {
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

}