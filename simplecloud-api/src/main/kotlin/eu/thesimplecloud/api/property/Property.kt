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

import eu.thesimplecloud.api.utils.DatabaseExclude
import eu.thesimplecloud.clientserverapi.lib.json.PacketExclude
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.jsonlib.JsonLibExclude


class Property<T : Any>(
    value: T
) : IProperty<T> {

    @JsonLibExclude
    @PacketExclude
    @DatabaseExclude
    @Volatile
    private var savedValue: T? = value

    private val className: String = value::class.java.name

    @Volatile
    private var valueAsString: String = JsonLib.fromObject(value).getAsJsonString()

    @DatabaseExclude
    @JsonLibExclude
    @Volatile
    var lastUpdateTimeStamp = 0L
        private set

    init {
        setLastUpdateToNow()
    }

    @Synchronized
    override fun getValue(): T {
        if (savedValue == null) {
            val clazz = propertyClassFindFunction(className) as Class<T>
            savedValue = JsonLib.fromJsonString(valueAsString).getObject(clazz)
        }
        return savedValue!!
    }

    fun resetValue() {
        this.savedValue = null
    }

    fun setLastUpdateToNow() {
        this.lastUpdateTimeStamp = System.currentTimeMillis()
    }

    override fun getValueAsString(): String {
        return this.valueAsString
    }

    fun setStringValue(string: String) {
        this.valueAsString = string
    }

    companion object {
        @Volatile
        var propertyClassFindFunction: (String) -> Class<*> = {
            Class.forName(it, true, Property::class.java.classLoader)
        }
    }

}