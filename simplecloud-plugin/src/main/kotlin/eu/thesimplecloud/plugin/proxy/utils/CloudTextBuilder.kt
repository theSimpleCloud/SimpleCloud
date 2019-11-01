package eu.thesimplecloud.plugin.proxy.utils

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import java.util.*


object CloudTextBuilder {

    private val colorCodes = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    private val specialCodes = Arrays.asList('k', 'l', 'm', 'n', 'o')

    fun build(cloudText: CloudText): TextComponent {
        val textComponent = TextComponent()
        var text = cloudText.getText().replaceAll("&", "§")

        var currentColorCode = Character.MIN_VALUE
        val currentSpecialCodes = ArrayList<Char>()
        val stringBuilder = StringBuilder()
        var i = 0
        while (i < text.length) {
            val c = text.get(i)
            if (c == '§') {
                i++
                if (i == text.length) {
                    i++
                    continue
                }
                val nextCode = text.get(i)
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
                currentSpecialCodes.forEach { character -> stringBuilder.append("§$character") }
                stringBuilder.append(c)
            }
            i++
        }
        text = stringBuilder.toString()

        textComponent.text = text
        if (cloudText.getHover() != null) {
            textComponent.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder(cloudText.getHover()).create())
        }
        if (cloudText.getText() != null && cloudText.getClick() != null) {
            textComponent.clickEvent = ClickEvent(ClickEvent.Action.valueOf(cloudText.getClickEventType().toString()), cloudText.getClick())
        }
        return textComponent
    }

}