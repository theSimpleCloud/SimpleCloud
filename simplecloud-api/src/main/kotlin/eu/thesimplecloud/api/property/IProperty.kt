package eu.thesimplecloud.api.property

interface IProperty<T : Any> {

    /**
     * Returns the value of this property
     * @param callerClassLoader the class loader of the caller class. The class loader is used to load the class.
     * This is required when this function gets called from a module.
     */
    fun getValue(callerClassLoader: ClassLoader): T

    /**
     * Returns the value of this property
     */
    fun getValue(): T = getValue(this::class.java.classLoader)

}