package eu.thesimplecloud.module.proxy.config

import eu.thesimplecloud.api.sync.`object`.ISynchronizedObject

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.03.2020
 * Time: 21:36
 */
data class Config(
        val proxyGroupConfigurations: List<ProxyGroupConfiguration>,
        val tablistConfigurations: List<TablistConfiguration>,
        val maintenanceKickMessage: String,
        val fullProxyKickMessage: String
) : ISynchronizedObject {

    override fun getName(): String {
        return "simplecloud-module-proxy-config"
    }

    companion object {
        fun getDefaultConfig(): Config {
            val motdConfiguration = MotdConfiguration(listOf("§cSimpleCloud service"),
                    listOf(""),
                    emptyList(),
                    null)
            val maintenanceMotdConfiguration = MotdConfiguration(listOf("§cSimpleCloud service"),
                    listOf("§cMaintenance"),
                    emptyList(),
                    null)
            val proxyGroupConfiguration = ProxyGroupConfiguration("Proxy",
                    512,
                    listOf("Fllip", "Wetterbericht"),
                    motdConfiguration,
                    maintenanceMotdConfiguration)

            val tablistConfiguration = TablistConfiguration(listOf("Proxy"),
                    listOf ("Header"),
                    listOf("Footer"),
                    1)

            val config = Config(listOf(proxyGroupConfiguration),
                    listOf(tablistConfiguration),
                    "This service is in maintenance",
                    "This service is full")

            return config
        }
    }
}