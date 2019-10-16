package eu.thesimplecloud.lib.parser.jsondata

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.parser.ITypeParser

interface IJsonDataTypeParser<R : Any> : ITypeParser<JsonData, R> {
}