package eu.thesimplecloud.api.property

interface IPropertyMap {

    /**
     * Returns all properties
     */
    fun getProperties(): Map<String, Property<*>>

    /**
     * Returns the property by the specified [name].
     */
    fun <T : Any> getProperty(name: String): Property<T>? = getProperties()[name] as Property<T>?

    /**
     * Sets the specified [property] as property linked to the specified [name].
     */
    fun <T : Any> setProperty(name: String, property: Property<T>)

    /**
     * Removes the [Property] found by the specified [name].
     */
    fun removeProperty(name: String)

    /**
     * Returns whether this player has the specified [property]
     */
    fun hasProperty(property: String) = getProperties().keys.contains(property)

}