package eu.thesimplecloud.module.proxy.extensions

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 16.03.2020
 * Time: 18:09
 */

fun List<String>.mapToLowerCase(): List<String> {
    return this.map { it.toLowerCase() }
}