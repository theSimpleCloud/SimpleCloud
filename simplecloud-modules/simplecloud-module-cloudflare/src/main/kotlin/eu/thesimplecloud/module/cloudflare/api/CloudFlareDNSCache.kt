package eu.thesimplecloud.module.cloudflare.api

import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 11.04.2020
 * Time: 19:57
 */
data class CloudFlareDNSCache(
        val dns: String,
        val uuid: UUID
)