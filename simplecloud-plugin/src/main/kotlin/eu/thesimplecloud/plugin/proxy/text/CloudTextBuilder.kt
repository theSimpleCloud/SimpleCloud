package eu.thesimplecloud.plugin.proxy.text

import eu.thesimplecloud.lib.player.text.CloudText
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import java.util.*
import java.util.function.Consumer

class CloudTextBuilder {

    private val colorCodes = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    private val specialCodes = Arrays.asList('k', 'l', 'm', 'n', 'o')

    fun build(cloudText: CloudText): TextComponent? {
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
        if (cloudText.hover != null) {
            textComponent.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder(cloudText.hover).create())
        }
        if (cloudText.click != null) {
            textComponent.clickEvent = ClickEvent(ClickEvent.Action.valueOf(cloudText.clickEventType.toString()), cloudText.click)
        }
        return textComponent
    }

}