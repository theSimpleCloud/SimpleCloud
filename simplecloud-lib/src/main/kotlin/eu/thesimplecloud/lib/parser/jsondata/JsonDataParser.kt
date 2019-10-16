package eu.thesimplecloud.lib.parser.jsondata

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.parser.ITypeFromClassParser

class JsonDataParser : ITypeFromClassParser<JsonData> {

    override fun supportedTypes(): Set<Class<out Any>> = emptySet()

    override fun <R : Any> parseToObject(value: JsonData, clazz: Class<R>): R? = null
}