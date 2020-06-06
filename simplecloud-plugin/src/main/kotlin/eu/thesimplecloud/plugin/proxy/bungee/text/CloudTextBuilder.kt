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

package eu.thesimplecloud.plugin.proxy.bungee.text

import eu.thesimplecloud.api.player.text.CloudText
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import java.util.*
import java.util.function.Consumer

class CloudTextBuilder {

    private val colorCodes = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    private val specialCodes = listOf('k', 'l', 'm', 'n', 'o')

    fun build(cloudText: CloudText): TextComponent {
        val textComponent = TextComponent()
        var text: String = cloudText.text.replace("&", "§")
        var currentColorCode = Character.MIN_VALUE
        val currentSpecialCodes: MutableList<Char> = ArrayList()
        val stringBuilder = StringBuilder()
        var i = 0
        while (i < text.length) {
            val c = text[i]
            if (c == '§') {
                i++
                if (i == text.length) {
                    i++
                    continue
                }
                val nextCode = text[i]
                if (colorCodes.contains(nextCode)) {
                    currentColorCode = nextCode
                    currentSpecialCodes.clear()
                    i++
                    continue
                }
                if (specialCodes.contains(nextCode)) {
                    currentSpecialCodes.add(nextCode)
                    i++
                    continue
                }
                if (nextCode == 'r') {
                    currentSpecialCodes.clear()
                }
            } else {
                stringBuilder.append("§$currentColorCode")
                currentSpecialCodes.forEach(Consumer { character: Char -> stringBuilder.append("§$character") })
                stringBuilder.append(c)
            }
            i++
        }
        text = stringBuilder.toString()
        textComponent.text = text

        val hover = cloudText.hover
        if (hover != null) {
            textComponent.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder(hover).create())
        }
        val click = cloudText.click
        if (click != null) {
            textComponent.clickEvent = ClickEvent(ClickEvent.Action.valueOf(cloudText.clickEventType.toString()), click)
        }

        val appendedCloudText = cloudText.appendedCloudText
        if (appendedCloudText != null) {
            val componentToAppend = build(appendedCloudText)
            textComponent.addExtra(componentToAppend)
        }

        return textComponent
    }

}