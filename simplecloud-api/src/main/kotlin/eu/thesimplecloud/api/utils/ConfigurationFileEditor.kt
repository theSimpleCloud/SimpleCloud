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

package eu.thesimplecloud.api.utils

import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Created by IntelliJ IDEA.
 * Date: 16.06.2021
 * Time: 09:26
 * @author Frederick Baier
 *
 * A simple file editor to edit configuration files
 * @param lines the lines of the files (all spaces at the beginning of lines will be cut off)
 * @param keyValueSplitter the splitter of name and value. For YML it would be ": "
 *
 */
class ConfigurationFileEditor(
    private val linesWithSpaces: List<String>,
    private val keyValueSplitter: String
) {

    constructor(file: File, keyValueSplitter: String) : this(file.readLines(StandardCharsets.UTF_8), keyValueSplitter)

    private val lines = this.linesWithSpaces.map { removeFirstSpaces(it) }

    private val keyToValues = HashMap(getKeyToValueMapByLines(this.lines))

    private fun getKeyToValueMapByLines(lines: List<String>): Map<String, String> {
        val keyValueSplitArrays = lines.filter { it.contains(keyValueSplitter) }.map { it.split(keyValueSplitter) }
        return keyValueSplitArrays.map { it[0] to (it.getOrNull(1) ?: "") }.toMap()
    }

    fun getValue(key: String): String? {
        return this.keyToValues[key]
    }

    fun setValue(key: String, value: String) {
        if (!this.keyToValues.containsKey(key)) throw IllegalStateException("Key '${key}' does not exist")
        this.keyToValues[key] = value
    }

    fun saveToFile(file: File) {
        val linesToSave = generateNewLines()
        file.writeText(linesToSave.joinToString("\n"))
    }

    private fun generateNewLines(): List<String> {
        val mutableLines = ArrayList(this.linesWithSpaces)
        for ((key, value) in this.keyToValues) {
            val lineIndex = getLineIndexByKey(key)
            val newLine = constructNewLine(key, value, lineIndex)
            mutableLines[lineIndex] = newLine
        }
        return mutableLines
    }

    private fun constructNewLine(key: String, value: String, lineIndex: Int): String {
        val lineWithoutSpaces = key + this.keyValueSplitter + value
        val amountOfStartSpaces = getAmountOfStartSpacesInLine(this.linesWithSpaces[lineIndex])
        val spacesString = getStringWithSpaces(amountOfStartSpaces)
        return spacesString + lineWithoutSpaces
    }

    private fun getStringWithSpaces(amount: Int): String {
        return (0 until amount).joinToString("") { " " }
    }

    private fun removeFirstSpaces(string: String): String {
        val amountOfSpaces = getAmountOfStartSpacesInLine(string)
        return string.drop(amountOfSpaces)
    }

    private fun getAmountOfStartSpacesInLine(line: String): Int {
        var line = line
        var amountOfSPaces = 0
        while (line.startsWith(" ")) {
            line = line.drop(1)
            amountOfSPaces++
        }
        return amountOfSPaces
    }

    private fun getLineIndexByKey(key: String): Int {
        val lineStart = key + this.keyValueSplitter
        val line = lines.firstOrNull { it.startsWith(lineStart) } ?: return -1
        return this.lines.indexOf(line)
    }

    companion object {
        const val YAML_SPLITTER = ": "
        const val PROPERTIES_SPLITTER = "="
        const val TOML_SPLITTER = " = "
    }


}