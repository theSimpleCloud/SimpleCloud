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

package eu.thesimplecloud.module.proxy.service.velocity

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.module.proxy.config.TablistConfiguration
import eu.thesimplecloud.module.proxy.service.ProxyHandler
import eu.thesimplecloud.module.proxy.service.velocity.listener.VelocityListener
import eu.thesimplecloud.plugin.proxy.velocity.text.CloudTextBuilder
import java.util.concurrent.TimeUnit

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 16.05.2020
 * Time: 23:37
 */

@Plugin(id = "simplecloud_proxy", dependencies = arrayOf(Dependency(id = "simplecloud_plugin")))
class VelocityPluginMain @Inject constructor(val proxyServer: ProxyServer) {

    var tablistStarted = false
    lateinit var proxyHandler: ProxyHandler


    @Subscribe
    fun handleInit(event: ProxyInitializeEvent) {
        proxyHandler = ProxyHandler()

        proxyHandler.onEnable()
        proxyServer.eventManager.register(this, VelocityListener(this))
    }

    private var headerCounter = 0
    private var footerCounter = 0

    fun startTablist() {

        tablistStarted = true
        proxyHandler.getTablistConfiguration()?.animationDelayInSeconds?.let {
            proxyServer.scheduler.buildTask(this) {
                val tablistConfiguration = proxyHandler.getTablistConfiguration() ?: return@buildTask

                if (proxyServer.allPlayers.isEmpty()) {
                    return@buildTask
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

            }.repeat(it, TimeUnit.SECONDS).schedule()
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
        proxyServer.allPlayers.forEach {
            if (it.currentServer.isPresent) {
                sendHeaderAndFooter(it, header, footer)
            }
        }
    }

    fun sendHeaderAndFooter(player: Player, header: String, footer: String) {
        val currentServer = player.currentServer
        if (currentServer.isPresent) {
            val serverName = currentServer.get().serverInfo.name
            player.tabList.setHeaderAndFooter(CloudTextBuilder().build(CloudText(proxyHandler.replaceString(header, serverName))),
                    CloudTextBuilder().build(CloudText(proxyHandler.replaceString(footer, serverName))))
        }
    }

}