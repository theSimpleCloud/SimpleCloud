/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.api.parser.string

import eu.thesimplecloud.api.parser.ITypeFromClassParser
import eu.thesimplecloud.api.parser.string.typeparser.*
import eu.thesimplecloud.api.utils.enumValueOf
import eu.thesimplecloud.api.utils.getEnumValues
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
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