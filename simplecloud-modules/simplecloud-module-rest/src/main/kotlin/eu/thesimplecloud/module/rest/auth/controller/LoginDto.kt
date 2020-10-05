package eu.thesimplecloud.module.rest.auth.controller

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 12.06.2020
 * Time: 17:46
 */
data class LoginDto(
    val username: String,
    val password: String
)