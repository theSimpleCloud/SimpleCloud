package eu.thesimplecloud.lib.stringparser

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.service.ServiceVersion
import eu.thesimplecloud.lib.stringparser.customparser.*
import eu.thesimplecloud.lib.utils.getEnumValues
import eu.thesimplecloud.lib.utils.enumValueOf
import java.lang.IllegalArgumentException
import java.util.*

class StringParser {

    private val parsableTypes = listOf(String::class.java, Int::class.java, UUID::class.java)

    private val customTypeParsers = mutableListOf(CloudLobbyGroupParser(), CloudProxyGroupParser(), CloudServerGroupParser(), CloudServiceGroupParser(),
            CloudServiceParser(), WrapperInfoParser(), BooleanParser(), TemplateParser())

    fun addCustomTypeParser(customTypeParser: ICustomTypeParser<out Any>) {
        this.customTypeParsers.add(customTypeParser)
    }

    fun <T : Any> parserString(string: String, clazz: Class<T>): T? {
        if (clazz.isEnum) {
            clazz as Class<out Enum<*>>
            val enumValues = clazz.getEnumValues()
            val indexOf = enumValues.map { it.toLowerCase() }.indexOf(string.toLowerCase())
            if (indexOf == -1)
                return null
            return clazz.enumValueOf(enumValues[indexOf]) as T
        }
        if (parsableTypes.contains(clazz)) {
            return JsonData.fromObject(string).getObject(clazz)
        }
        val parser = customTypeParsers.firstOrNull { it.allowedTypes().contains(clazz) }
        parser ?: throw IllegalArgumentException("Can't parse class to ${clazz.simpleName}: No parser found.")
        parser as ICustomTypeParser<out T>
        return parser.parse(string)
    }

}