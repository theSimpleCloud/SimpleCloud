package eu.thesimplecloud.api.parser.jsondata

import eu.thesimplecloud.api.parser.ITypeParser
import eu.thesimplecloud.clientserverapi.lib.json.JsonData

interface IJsonDataTypeParser<R : Any> : ITypeParser<JsonData, R> {
}