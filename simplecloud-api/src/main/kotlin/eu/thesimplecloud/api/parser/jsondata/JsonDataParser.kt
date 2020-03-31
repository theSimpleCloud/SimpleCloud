package eu.thesimplecloud.api.parser.jsondata

import eu.thesimplecloud.api.parser.ITypeFromClassParser
import eu.thesimplecloud.clientserverapi.lib.json.JsonData

class JsonDataParser : ITypeFromClassParser<JsonData> {

    override fun supportedTypes(): Set<Class<out Any>> = emptySet()

    override fun <R : Any> parseToObject(value: JsonData, clazz: Class<R>): R? = null
}