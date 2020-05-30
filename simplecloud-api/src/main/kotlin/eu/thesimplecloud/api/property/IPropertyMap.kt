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
     * Removes the [Property] found by the specified [name].
     */
    fun removeProperty(name: String)

    /**
     * Returns whether this player has the specified [property]
     */
    fun hasProperty(property: String) = getProperties().keys.contains(property)

}