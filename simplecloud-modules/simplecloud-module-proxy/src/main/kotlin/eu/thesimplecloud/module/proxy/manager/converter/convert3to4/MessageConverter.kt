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