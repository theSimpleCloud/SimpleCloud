/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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

package eu.thesimplecloud.module.proxy.manager.converter.convert3to4

/**
 * Date: 18.06.22
 * Time: 21:03
 * @author Frederick Baier
 *
 */
class MessageConverter(
    private var line: String
) {

    private val colorReplacements = mapOf(
        "§0" to "<black>",
        "§1" to "<dark_blue>",
        "§2" to "<dark_green>",
        "§3" to "<dark_aqua>",
        "§4" to "<dark_red>",
        "§5" to "<dark_purple>",
        "§6" to "<gold>",
        "§7" to "<gray>",
        "§8" to "<dark_gray>",
        "§9" to "<blue>",
        "§a" to "<green>",
        "§b" to "<aqua>",
        "§c" to "<red>",
        "§d" to "<light_purple>",
        "§e" to "<yellow>",
        "§f" to "<white>",
    )

    fun convert(): String {
        replaceBraces()
        replaceColors()
        replaceOtherColorCodes()
        return this.line
    }

    private fun replaceOtherColorCodes() {
        line = SpecialColorCodeReplacer(line).convert()
    }

    private fun replaceColors() {
        for ((oldColor, newColor) in colorReplacements) {
            line = line.replace(oldColor, newColor)
        }
    }

    private fun replaceBraces() {
        line = line.replace("\\u003c", "<").replace("\\u003e", ">")
    }

}