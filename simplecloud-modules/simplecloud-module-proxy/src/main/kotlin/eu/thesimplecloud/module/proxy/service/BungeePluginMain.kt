package eu.thesimplecloud.module.proxy.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.module.proxy.config.Config
import eu.thesimplecloud.module.proxy.config.ProxyGroupConfiguration
import eu.thesimplecloud.module.proxy.config.TablistConfiguration
import eu.thesimplecloud.module.proxy.service.listener.BungeeListener
import eu.thesimplecloud.plugin.proxy.text.CloudTextBuilder
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Plugin
import java.util.concurrent.TimeUnit


/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.03.2020
 * Time: 21:33
 */
class BungeePluginMain : Plugin() {

    var config: Config = Config.getDefaultConfig()
    val thisService = CloudPlugin.instance.thisService()
    val serviceGroupName = thisService.getGroupName()

    var tablistStarted = false

    override fun onEnable() {
        CloudAPI.instance.getSynchronizedObjectManager()
                .requestSynchronizedObject("simplecloud-module-proxy-config", Config::class.java)
                .addResultListener {
                    config = it
                }

        proxy.pluginManager.registerListener(this, BungeeListener(this))
    }

    fun startTablist() {
        val tablistConfiguration = getTablistConfiguration() ?: return

        var counter = 0

        tablistStarted = true
        proxy.scheduler.schedule(this, Runnable {

            if (proxy.players.size == 0) {
                return@Runnable
            }
            var header: String = ""
            var footer: String = ""

            var onlinePlayers = getOnlinePlayers()

            if (counter >= tablistConfiguration.footers.size) {
                counter = 0
            }

            if (tablistConfiguration.footers.isNotEmpty()) {
                footer = tablistConfiguration.footers.get(counter)
                if (tablistConfiguration.headers.size > counter) {
                    header = tablistConfiguration.headers.get(counter)
                } else if (tablistConfiguration.headers.isNotEmpty()) {
                    header = tablistConfiguration.headers.get(0)
                }
            }

            sendHeaderAndFooter(header, footer)

            counter++
        }, tablistConfiguration.animationDelayInSeconds, 1, TimeUnit.SECONDS)
    }

    fun sendHeaderAndFooter(header: String, footer: String) {
        proxy.players.forEach {
            it.setTabHeader(CloudTextBuilder().build(CloudText(replaceString(header, it)))
                    , CloudTextBuilder().build(CloudText(replaceString(footer, it))))
        }
    }

    fun getTablistConfiguration(): TablistConfiguration? {
        return config.tablistConfigurations.firstOrNull {
            it.proxies
                    .map { it.toLowerCase() }.contains(serviceGroupName.toLowerCase())
        }
    }

    fun getProxyConfiguration(): ProxyGroupConfiguration? {
        return config.proxyGroupConfigurations.firstOrNull { it.proxyGroup == serviceGroupName }
    }

    fun getOnlinePlayers(): Int {
        var onlinePlayers = 0
        thisService.getServiceGroup().getAllServices().forEach { onlinePlayers += it.getOnlinePlayers() }
        return onlinePlayers
    }

    fun replaceString(message: String): String {
        val configuration = getProxyConfiguration() ?: return ""

        val replacesMessage = message
                .replace("%ONLINE_PLAYERS%", getOnlinePlayers().toString())
                .replace("%MAX_PLAYERS%", configuration.maxPlayers.toString())
                .replace("%PROXY%", thisService.getName())

        return ChatColor.translateAlternateColorCodes('&', replacesMessage)
    }

    fun replaceString(message: String, player: ProxiedPlayer): String {
        return replaceString(message)
                .replace("%SERVER%", player.server.info.name)
    }

}