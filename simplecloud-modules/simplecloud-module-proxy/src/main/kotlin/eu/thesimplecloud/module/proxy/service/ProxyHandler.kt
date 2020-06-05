/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
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

    val JOIN_MAINTENANCE_PERMISSION = "cloud.maintenance.join"
    val JOIN_FULL_PERMISSION = "cloud.full.join"


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
        return CloudPlugin.instance.thisService().getServiceGroup().getOnlinePlayerCount()
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