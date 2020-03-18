package eu.thesimplecloud.api.utils

import java.io.Serializable

@FunctionalInterface
interface NoArgsFunction<R> : Serializable {

    /**
     * Represents a function that needs no parameters and returns the type [R]
     */
    operator fun invoke(): R

}