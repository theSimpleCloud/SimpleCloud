package eu.thesimplecloud.module.proxy.config

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.03.2020
 * Time: 21:54
 */

data class ProxyGroupConfiguration(
        val proxyGroup: String,
        val whitelist: MutableList<String>,
        val motds: MotdConfiguration,
        val maintenanceMotds: MotdConfiguration
)