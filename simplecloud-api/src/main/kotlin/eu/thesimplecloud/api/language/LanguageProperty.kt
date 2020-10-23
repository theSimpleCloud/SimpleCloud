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

package eu.thesimplecloud.api.language

class LanguageProperty(val property: String, val message: String) {


    /**
     * Returns all placeholders in this message.
     *
     * Messages look like this
     * Service %SERVICE% was stopped.
     */
    fun getAllPlaceHolders(): List<LanguagePlaceholder> {
        val percentParts = this.message.split("%")
        //is number not divisible by 2
        if (percentParts.lastIndex % 2 != 0) {
            throw IllegalArgumentException("Invalid message format for property $property")
        }

        val returnList = mutableListOf<LanguagePlaceholder>()
        //all indices of placeholders
        for (i in 1..percentParts.lastIndex step 2) {
            returnList.add(LanguagePlaceholder(percentParts[i]))
        }
        return returnList
    }

    fun getReplacedMessage(vararg replacements: String): String {
        val allPlaceHolders = getAllPlaceHolders()
        var message = this.message
        val replacementsIterator = replacements.iterator()
        for (placeHolder in allPlaceHolders) {
            message = message.replace(placeHolder.getStringToReplace(), replacementsIterator.next())
        }
        return message
    }

}