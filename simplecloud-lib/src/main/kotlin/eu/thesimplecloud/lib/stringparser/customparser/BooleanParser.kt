package eu.thesimplecloud.lib.stringparser.customparser

import eu.thesimplecloud.lib.stringparser.ICustomTypeParser

class BooleanParser : ICustomTypeParser<Boolean> {

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