package eu.thesimplecloud.module.sign.lib

class SignFrame(
       val lines: Array<String>
) {

    init {
        if (lines.size != 4) throw IllegalStateException("Frame must have 4 lines")
    }
}