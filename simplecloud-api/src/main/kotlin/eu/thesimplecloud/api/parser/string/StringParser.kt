package eu.thesimplecloud.api.parser.string

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.api.parser.ITypeFromClassParser
import eu.thesimplecloud.api.parser.string.typeparser.*
import eu.thesimplecloud.api.utils.getEnumValues
import eu.thesimplecloud.api.utils.enumValueOf
import java.lang.IllegalArgumentException
import java.util.*

class StringParser : ITypeFromClassParser<String>{

    private val parsableTypes = listOf(String::class.java, Int::class.java, UUID::class.java)

    private val customTypeParsers = mutableListOf(CloudLobbyGroupParser(), CloudProxyGroupParser(), CloudServerGroupParser(), CloudServiceGroupParser(),
            CloudServiceParser(), WrapperInfoParser(), BooleanParser(), TemplateParser())

    override fun supportedTypes(): Set<Class<out Any>> = customTypeParsers.map { it.allowedTypes() }.flatten().union(parsableTypes)

    override fun <R : Any> parseToObject(string: String, clazz: Class<R>): R? {
        if (clazz.isEnum) {
            clazz as Class<out Enum<*>>
            val enumValues = clazz.getEnumValues()
            val indexOf = enumValues.map { it.toLowerCase() }.indexOf(string.toLowerCase())
            if (indexOf == -1)
                return null
            return clazz.enumValueOf(enumValues[indexOf]) as R
        }
        if (parsableTypes.contains(clazz)) {
            return JsonData.fromObject(string).getObjectOrNull(clazz)
        }
        val parser = customTypeParsers.firstOrNull { it.allowedTypes().contains(clazz) }
        parser ?: throw IllegalArgumentException("Can't parse class to ${clazz.simpleName}: No parser found.")
        parser as IStringTypeParser<out R>
        return parser.parse(string)
    }

}