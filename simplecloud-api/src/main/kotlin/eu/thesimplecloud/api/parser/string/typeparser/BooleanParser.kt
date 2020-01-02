package eu.thesimplecloud.api.parser.string.typeparser

import eu.thesimplecloud.api.parser.string.IStringTypeParser

class BooleanParser : IStringTypeParser<Boolean> {

    override fun allowedTypes(): List<Class<out Boolean>> = listOf(Boolean::class.java)

    override fun parse(string: String): Boolean? {
        when (string.toLowerCase()){
            "yes" -> return true
            "no" -> return false
            "true" -> return true
            "false" -> return false
        }
        return null
    }
}