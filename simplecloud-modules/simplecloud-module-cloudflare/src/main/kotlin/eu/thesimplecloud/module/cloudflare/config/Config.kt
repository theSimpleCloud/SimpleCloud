package eu.thesimplecloud.module.cloudflare.config

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.02.2020
 * Time: 17:37
 */
data class Config(
        val email: String,
        val apiToken: String,
        val domainName: String,
        val zoneID: String,
        val subDomain: String
)