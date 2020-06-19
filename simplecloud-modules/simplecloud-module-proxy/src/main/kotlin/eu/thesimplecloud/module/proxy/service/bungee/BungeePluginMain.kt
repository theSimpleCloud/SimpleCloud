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
    }

    private var headerCounter = 0
    private var footerCounter = 0

    fun startTablist() {

        tablistStarted = true
        proxyHandler.getTablistConfiguration()?.animationDelayInSeconds?.let {
            proxy.scheduler.schedule(this, Runnable {
                val tablistConfiguration = proxyHandler.getTablistConfiguration() ?: return@Runnable

                if (proxy.players.isEmpty()) {
                    return@Runnable
                }

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

    private fun sendHeaderAndFooter(header: String, footer: String) {
        proxy.players.forEach {
            if (it.server != null)
                sendHeaderAndFooter(it, header, footer)
        }
    }

    fun sendHeaderAndFooter(player: ProxiedPlayer, header: String, footer: String) {
        if (player.server == null) return
        val serverName = player.server.info.name
        player.setTabHeader(CloudTextBuilder().build(CloudText(proxyHandler.replaceString(header, serverName))),
                CloudTextBuilder().build(CloudText(proxyHandler.replaceString(footer, serverName))))
    }

}