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

interface IPropertyMap {

    /**
     * Returns all properties
     */
    fun getProperties(): Map<String, IProperty<*>>

    /**
     * Returns the property by the specified [name].
     */
    fun <T : Any> getProperty(name: String): IProperty<T>? = getProperties()[name] as IProperty<T>?

    /**
     * Sets the specified [value] as property linked to the specified [name].
     * @return the created property
     */
    fun <T : Any> setProperty(name: String, value: T): IProperty<T>

    /**
     * Clears all set properties
     */
    fun clearProperties()

    /**
     * Removes the [Property] found by the specified [name].
     */
    fun removeProperty(name: String)

    /**
     * Returns whether this player has the specified [property]
     */
    fun hasProperty(property: String) = getProperties().keys.contains(property)

    /**
     * Returns the newer property according to [Property.lastUpdateTimeStamp]
     */
    private fun getNewerProperty(propertyOne: IProperty<*>, propertyTwo: IProperty<*>): IProperty<*> {
        propertyOne as Property<*>
        propertyTwo as Property<*>
        return if (propertyOne.lastUpdateTimeStamp > propertyTwo.lastUpdateTimeStamp) {
            propertyOne
        } else {
            propertyTwo
        }
    }

    fun getMapWithNewestProperties(compareMap: Map<String, IProperty<*>>): HashMap<String, IProperty<*>> {
        val ownMap = getProperties()
        val allKeys = ownMap.keys.union(compareMap.keys)
        val map = HashMap<String, IProperty<*>>()
        for (key in allKeys) {
            val valueOne = ownMap[key] as Property<*>?
            val valueTwo = compareMap[key] as Property<*>?
            //choose 0 as default value because it will be older than the other value and one of these value must be non-null
            val valueOneLastUpdate = valueOne?.lastUpdateTimeStamp ?: 0
            val valueTwoLastUpdate = valueTwo?.lastUpdateTimeStamp ?: 0
            map[key] = if (valueOneLastUpdate > valueTwoLastUpdate) valueOne!! else valueTwo!!
        }
        return map
    }
}