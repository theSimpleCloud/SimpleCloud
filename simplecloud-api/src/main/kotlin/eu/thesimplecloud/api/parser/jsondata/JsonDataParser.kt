package eu.thesimplecloud.api.parser.jsondata

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.api.parser.ITypeFromClassParser

class JsonDataParser : ITypeFromClassParser<JsonData> {

    override fun supportedTypes(): Set<Class<out Any>> = emptySet()

    override fun <R : Any> parseToObject(value: JsonData, clazz: Class<R>): R? = null
}