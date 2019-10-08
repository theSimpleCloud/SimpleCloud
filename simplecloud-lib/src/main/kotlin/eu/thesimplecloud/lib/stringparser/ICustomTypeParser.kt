package eu.thesimplecloud.lib.stringparser

import kotlin.reflect.KClass


interface ICustomTypeParser<T : Any> {

    /**
     * Returns a list of type this parser can parse
     */
    fun allowedTypes(): List<KClass<out T>>

    /**
     * Parses a [String] to the type [T]
     */
    fun parse(string: String): T?

}