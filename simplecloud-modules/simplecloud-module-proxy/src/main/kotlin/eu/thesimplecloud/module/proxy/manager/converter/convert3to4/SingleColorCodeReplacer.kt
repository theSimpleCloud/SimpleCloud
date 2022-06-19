package eu.thesimplecloud.module.proxy.manager.converter.convert3to4

/**
 * Date: 19.06.22
 * Time: 10:01
 * @author Frederick Baier
 *
 */
class SingleColorCodeReplacer(
    private val colorCode: Char,
    private val colorCodeName: String
) {

    private var foundColorCode: Boolean = false

    fun getColorCode(): Char {
        return this.colorCode
    }

    fun foundColorCode(stringBuilder: StringBuilder) {
        stringBuilder.append("<${colorCodeName}>")
        this.foundColorCode = true
    }

    fun endIfOpen(stringBuilder: StringBuilder) {
        if (foundColorCode) {
            foundColorCode = false
            stringBuilder.append("</${colorCodeName}>")
        }
    }

}