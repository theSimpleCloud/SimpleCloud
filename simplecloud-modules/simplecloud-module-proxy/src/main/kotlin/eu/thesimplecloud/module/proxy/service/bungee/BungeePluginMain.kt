/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.module.proxy.service.bungee

import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.module.proxy.config.TablistConfiguration
import eu.thesimplecloud.module.proxy.service.ProxyHandler
import eu.thesimplecloud.module.proxy.service.bungee.listener.BungeeListener
import eu.thesimplecloud.plugin.proxy.bungee.text.CloudTextBuilder
import net.md_5.bungee.api.ProxyServer
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


    var tablistStarted = false
    lateinit var proxyHandler: ProxyHandler

    override fun onEnable() {
        proxyHandler = ProxyHandler()

        proxyHandler.onEnable()
        proxy.pluginManager.registerListener(this, BungeeListener(this))
        startScheduler()
    }

    private fun startScheduler() {
        ProxyServer.getInstance().scheduler.schedule(this, {
            val tablistConfiguration = proxyHandler.getTablistConfiguration() ?: return@schedule
            ProxyServer.getInstance().players.forEach {
                sendHeaderAndFooter(it, tablistConfiguration)
            }
        }, 1, 1, TimeUnit.SECONDS)
    }

    fun sendHeaderAndFooter(proxiedPlayer: ProxiedPlayer, tablistConfiguration: TablistConfiguration) {
        val footerString = tablistConfiguration.footers.joinToString("\n")
        val headerString = tablistConfiguration.headers.joinToString("\n")
        sendHeaderAndFooter(proxiedPlayer, headerString, footerString)
    }

    fun sendHeaderAndFooter(player: ProxiedPlayer, header: String, footer: String) {
        if (player.server == null) return
        val serverName = player.server.info.name
        player.setTabHeader(CloudTextBuilder().build(CloudText(proxyHandler.replaceString(header, serverName))),
                CloudTextBuilder().build(CloudText(proxyHandler.replaceString(footer, serverName))))
    }

}