package eu.thesimplecloud.api.property

interface IProperty {

    /**
     * Returns the property by the specified [name].
     */
    fun <T : Any> getProperty(name: String): T?

    /**
     * Sets the specified [value] as property linked to the specified [name].
     */
    fun <T : Any> setProperty(name: String, value: T)

}