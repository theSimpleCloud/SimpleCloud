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