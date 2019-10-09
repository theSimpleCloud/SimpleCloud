package eu.thesimplecloud.lib.stringparser

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.stringparser.customparser.*
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.reflect.KClass

class StringParser {

    private val parsableTypes = listOf(String::class.java, Int::class.java, UUID::class.java)

    private val customTypeParsers = mutableListOf(CloudLobbyGroupParser(), CloudProxyGroupParser(), CloudServerGroupParser(), CloudServiceGroupParser(),
            CloudServiceParser(), WrapperInfoParser(), BooleanParser())

    fun addCustomTypeParser(customTypeParser: ICustomTypeParser<out Any>) {
        this.customTypeParsers.add(customTypeParser)
    }

    fun <T : Any> parserString(string: String, clazz: Class<T>): T? {
        if (clazz.isEnum || parsableTypes.contains(clazz)) {
            return JsonData.fromObject(string).getObject(clazz)
        }
        val parser = customTypeParsers.firstOrNull { it.allowedTypes().contains(clazz) }
        parser ?: throw IllegalArgumentException("Can't parse class to ${clazz.simpleName}: No parser found.")
        parser as ICustomTypeParser<out T>
        return parser.parse(string)
    }

}