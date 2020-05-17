package eu.thesimplecloud.module.proxy.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.sync.`object`.SynchronizedObjectHolder
import eu.thesimplecloud.module.proxy.config.Config
import eu.thesimplecloud.module.proxy.config.DefaultConfig
import eu.thesimplecloud.module.proxy.config.ProxyGroupConfiguration
import eu.thesimplecloud.module.proxy.config.TablistConfiguration
import eu.thesimplecloud.module.proxy.extensions.mapToLowerCase
import eu.thesimplecloud.plugin.startup.CloudPlugin

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 16.05.2020
 * Time: 23:43
 */
class ProxyHandler() {

    val JOIN_MAINTENANCE_PERMISSION = "simplecloud.maintenance.join"
    val JOIN_FULL_PERMISSION = "simplecloud.full.join"


    var configHolder: SynchronizedObjectHolder<Config> = SynchronizedObjectHolder(DefaultConfig.get())

    fun onEnable() {
        CloudAPI.instance.getSingleSynchronizedObjectManager()
                .requestSingleSynchronizedObject("simplecloud-module-proxy-config", Config::class.java)
                .addResultListener {
                    configHolder = it
                }
    }

    fun getTablistConfiguration(): TablistConfiguration? {
        return configHolder.obj.tablistConfigurations.firstOrNull {
            it.proxies.mapToLowerCase().contains(CloudPlugin.instance.thisService().getGroupName().toLowerCase())
        }
    }

    fun getProxyConfiguration(): ProxyGroupConfiguration? {
        return configHolder.obj.proxyGroupConfigurations.firstOrNull { it.proxyGroup == CloudPlugin.instance.thisService().getGroupName() }
    }

    fun getOnlinePlayers(): Int {
        return CloudPlugin.instance.thisService().getServiceGroup().getOnlineCount()
    }

    fun replaceString(message: String): String {

        return message
                .replace("%ONLINE_PLAYERS%", getOnlinePlayers().toString())
                .replace("%MAX_PLAYERS%", CloudPlugin.instance.thisService().getMaxPlayers().toString())
                .replace("%PROXY%", CloudPlugin.instance.thisService().getName())
    }

    fun replaceString(message: String, serverName: String): String {
        return replaceString(message)
                .replace("%SERVER%", serverName)
    }

}