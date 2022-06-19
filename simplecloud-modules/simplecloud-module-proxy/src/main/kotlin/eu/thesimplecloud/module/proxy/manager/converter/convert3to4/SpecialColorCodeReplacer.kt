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
 * Time: 21:20
 * @author Frederick Baier
 *
 */
class SpecialColorCodeReplacer(
    private var line: String
) {

    private val charArray = line.toCharArray()

    private val colorReplacements = listOf(
        SingleColorCodeReplacer('k', "obfuscated"),
        SingleColorCodeReplacer('l', "bold"),
        SingleColorCodeReplacer('m', "strikethrough"),
        SingleColorCodeReplacer('n', "underline"),
        SingleColorCodeReplacer('o', "italic"),
    )


    public fun convert(): String {
        val stringBuilder = StringBuilder()

        var lastCharWasParagraph = false
        for (charIndex in charArray.indices) {
            val currentChar = this.charArray[charIndex]
            if (currentChar == '§') {
                lastCharWasParagraph = true
                continue
            }
            if (!lastCharWasParagraph) {
                stringBuilder.append(currentChar)
                continue
            }
            //currentCharIsColorCode
            lastCharWasParagraph = false
            if (currentChar == 'r') {
                endOpenColors(stringBuilder)
                continue
            }
            val colorCodeReplacer = findColorReplacement(currentChar)
            colorCodeReplacer?.foundColorCode(stringBuilder)
        }
        return stringBuilder.toString()
    }

    private fun endOpenColors(stringBuilder: StringBuilder) {
        this.colorReplacements.forEach {
            it.endIfOpen(stringBuilder)
        }
    }

    private fun findColorReplacement(char: Char): SingleColorCodeReplacer? {
        return this.colorReplacements.firstOrNull { it.getColorCode() == char }
    }

}