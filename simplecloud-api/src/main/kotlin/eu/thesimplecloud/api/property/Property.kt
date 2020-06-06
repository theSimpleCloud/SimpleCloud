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

package eu.thesimplecloud.api.property

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.thesimplecloud.clientserverapi.lib.json.GsonExclude
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.json.PacketExclude


class Property<T : Any>(
        value: T
) : IProperty<T> {
    @GsonExclude
    @PacketExclude
    @Volatile
    var savedValue: T? = value

    val className = value::class.java.name

    @JsonIgnore
    private val valueAsString: String = JsonData.fromObject(value).getAsJsonString()

    @JsonIgnore
    @Synchronized
    override fun getValue(callerClassLoader: ClassLoader): T {
        if (savedValue == null) {
            val clazz = Class.forName(
                    className,
                    true,
                    callerClassLoader
            ) as Class<T>
            savedValue = JsonData.fromJsonString(valueAsString).getObject(clazz)
        }
        return savedValue!!
    }

    @JsonIgnore
    fun resetValue() {
        this.savedValue = null
    }

}