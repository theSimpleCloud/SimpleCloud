package eu.thesimplecloud.module.proxy.config

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.03.2020
 * Time: 21:40
 */
data class TablistConfiguration(
        val proxies: List<String>,
        val headers: List<String>,
        val footers: List<String>,
        val animationDelayInSeconds: Long
)