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
}