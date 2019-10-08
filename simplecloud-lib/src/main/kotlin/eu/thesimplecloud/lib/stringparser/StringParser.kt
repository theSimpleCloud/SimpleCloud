package eu.thesimplecloud.lib.stringparser

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.stringparser.customparser.*
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.reflect.KClass

class StringParser {

    private val parsableTypes = listOf(String::class, Int::class, UUID::class, Boolean::class)

    private val customTypeParsers = mutableListOf(CloudLobbyGroupParser(), CloudProxyGroupParser(), CloudServerGroupParser(), CloudServiceGroupParser(),
            CloudServiceParser(), WrapperInfoParser())

    fun addCustomTypeParser(customTypeParser: ICustomTypeParser<out Any>) {
        this.customTypeParsers.add(customTypeParser)
    }

    fun <T : Any> parserString(string: String, clazz: KClass<T>): T? {
        if (clazz.java.isEnum || parsableTypes.contains(clazz)) {
            return JsonData.fromObject(string).getObject(clazz.java)
        }
        val parser = customTypeParsers.firstOrNull { it.allowedTypes().contains(clazz) }
        parser ?: throw IllegalArgumentException("Can't parse class to ${clazz.java.simpleName}: No parser found.")
        parser as ICustomTypeParser<out T>
        return parser.parse(string)
    }

}