package eu.thesimplecloud.module.proxy.config

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 16.03.2020
 * Time: 18:30
 */
class DefaultConfig {
    companion object {
        fun get(): Config {
            val motdConfiguration = MotdConfiguration(listOf("§cSimpleCloud service"),
                    listOf(""),
                    emptyList(),
                    null)
            val maintenanceMotdConfiguration = MotdConfiguration(listOf("§cSimpleCloud service"),
                    listOf("§cMaintenance"),
                    emptyList(),
                    null)
            val proxyGroupConfiguration = ProxyGroupConfiguration("Proxy",
                    mutableListOf("Fllip", "Wetterbericht"),
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