package eu.thesimplecloud.api.parser

interface ITypeFromClassParser<T : Any> {

    /**
     * Returns a set of types this parser supports.
     */
    fun supportedTypes(): Set<Class<out Any>>

    /**
     * Parses the specified [value] to the specified [clazz]
     * Returns null if the value could not be parsed
     */
    fun <R : Any> parseToObject(value: T, clazz: Class<R>): R?

}