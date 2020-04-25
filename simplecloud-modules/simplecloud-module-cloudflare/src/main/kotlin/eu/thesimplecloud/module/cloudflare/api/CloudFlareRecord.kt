package eu.thesimplecloud.module.cloudflare.api

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 11.04.2020
 * Time: 19:37
 */
data class CloudFlareRecord(
        val serviceName: String,
        val recordID: String
)