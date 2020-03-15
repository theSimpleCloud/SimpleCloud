package eu.thesimplecloud.module.proxy.config

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.03.2020
 * Time: 21:54
 */

data class ProxyGroupConfiguration(
        val proxyGroup: String,
        val maxPlayers: Int,
        val whitelist: List<String>,
        val motds: MotdConfiguration,
        val maintenanceMotds: MotdConfiguration
)