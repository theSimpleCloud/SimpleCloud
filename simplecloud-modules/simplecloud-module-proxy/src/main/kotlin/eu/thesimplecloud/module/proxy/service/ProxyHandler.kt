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

package eu.thesimplecloud.module.proxy.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.module.proxy.config.Config
import eu.thesimplecloud.module.proxy.config.DefaultConfig
import eu.thesimplecloud.module.proxy.config.ProxyGroupConfiguration
import eu.thesimplecloud.module.proxy.config.TablistConfiguration
import eu.thesimplecloud.module.proxy.extensions.mapToLowerCase
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 16.05.2020
 * Time: 23:43
 */
class ProxyHandler {

    var configHolder: IProperty<Config> = Property(DefaultConfig.get())

    fun onEnable() {
        CloudAPI.instance.getGlobalPropertyHolder()
                .requestProperty<Config>("simplecloud-module-proxy-config")
                .addResultListener {
                    configHolder = it
                }
    }

    fun getTablistConfiguration(): TablistConfiguration? {
        return configHolder.getValue().tablistConfigurations.firstOrNull {
            it.proxies.mapToLowerCase().contains(CloudPlugin.instance.thisService().getGroupName().toLowerCase())
        }
    }

    fun getProxyConfiguration(): ProxyGroupConfiguration? {
        return configHolder.getValue().proxyGroupConfigurations.firstOrNull { it.proxyGroup == CloudPlugin.instance.thisService().getGroupName() }
    }

    fun getOnlinePlayers(): Int {
        return CloudPlugin.instance.thisService().getServiceGroup().getOnlinePlayerCount()
    }

    fun getHexColorComponent(message: String): TextComponent {
        return TextComponent.ofChildren(MiniMessage.get()
            .parse(message))
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

    companion object {
        const val JOIN_MAINTENANCE_PERMISSION = "cloud.maintenance.join"
        const val JOIN_FULL_PERMISSION = "cloud.full.join"
    }

}