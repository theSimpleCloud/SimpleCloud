package eu.thesimplecloud.api.parser.jsondata

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.api.parser.ITypeParser

interface IJsonDataTypeParser<R : Any> : ITypeParser<JsonData, R> {
}