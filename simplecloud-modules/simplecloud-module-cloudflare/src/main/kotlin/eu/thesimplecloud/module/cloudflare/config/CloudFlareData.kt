package eu.thesimplecloud.module.cloudflare.config

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 11.04.2020
 * Time: 19:31
 */
data class CloudFlareData(
        val groupName: String,
        val email: String,
        val apiToken: String,
        val domainName: String,
        val zoneID: String,
        val subDomain: String)