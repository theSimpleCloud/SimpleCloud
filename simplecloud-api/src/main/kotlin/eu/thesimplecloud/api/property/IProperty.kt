package eu.thesimplecloud.api.property

interface IProperty<T : Any> {

    /**
     * Returns the value of this property
     */
    fun getValue(callerClassLoader: ClassLoader = this::class.java.classLoader): T

}