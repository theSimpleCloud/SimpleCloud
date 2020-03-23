package eu.thesimplecloud.module.proxy.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.module.proxy.config.Config
import eu.thesimplecloud.module.proxy.config.DefaultConfig
import eu.thesimplecloud.module.proxy.config.ProxyGroupConfiguration
import eu.thesimplecloud.module.proxy.config.TablistConfiguration
import eu.thesimplecloud.module.proxy.extensions.mapToLowerCase
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

    var config: Config = DefaultConfig.get()
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

    private var headerCounter = 0
    private var footerCounter = 0

    fun startTablist() {

        tablistStarted = true
        getTablistConfiguration()?.animationDelayInSeconds?.let {
            proxy.scheduler.schedule(this, Runnable {
                val tablistConfiguration = getTablistConfiguration() ?: return@Runnable

                if (proxy.players.isEmpty()) {
                    return@Runnable
                }

                var onlinePlayers = getOnlinePlayers()

                headerCounter++
                footerCounter++

                if (headerCounter >= tablistConfiguration.headers.size) {
                    headerCounter = 0
                }

                if (footerCounter >= tablistConfiguration.footers.size) {
                    footerCounter = 0
                }
                val headerAndFooter = getCurrentHeaderAndFooter(tablistConfiguration)

                sendHeaderAndFooter(headerAndFooter.first, headerAndFooter.second)

            }, it, it, TimeUnit.SECONDS)
        }
    }

    fun getCurrentHeaderAndFooter(tablistConfiguration: TablistConfiguration): Pair<String, String> {
        var header: String = ""
        var footer: String = ""

        if (tablistConfiguration.footers.isNotEmpty()) {
            footer = tablistConfiguration.footers.get(footerCounter)
            if (tablistConfiguration.headers.size > headerCounter) {
                header = tablistConfiguration.headers.get(headerCounter)
            } else if (tablistConfiguration.headers.isNotEmpty()) {
                header = tablistConfiguration.headers.get(0)
            }
        }

        return Pair(header, footer)
    }

    fun sendHeaderAndFooter(header: String, footer: String) {
        proxy.players.forEach {
            sendHeaderAndFooter(it, header, footer)
        }
    }

    fun sendHeaderAndFooter(player: ProxiedPlayer, header: String, footer: String) {
        player.setTabHeader(CloudTextBuilder().build(CloudText(replaceString(header, player)))
                , CloudTextBuilder().build(CloudText(replaceString(footer, player))))
    }

    fun getTablistConfiguration(): TablistConfiguration? {
        return config.tablistConfigurations.firstOrNull {
            it.proxies.mapToLowerCase().contains(serviceGroupName.toLowerCase())
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
                .replace("%MAX_PLAYERS%", thisService.getMaxPlayers().toString())
                .replace("%PROXY%", thisService.getName())

        return ChatColor.translateAlternateColorCodes('&', replacesMessage)
    }

    fun replaceString(message: String, player: ProxiedPlayer): String {
        return replaceString(message)
                .replace("%SERVER%", player.server.info.name)
    }

}