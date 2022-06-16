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

package eu.thesimplecloud.module.proxy.config

import eu.thesimplecloud.api.CloudAPI

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 16.03.2020
 * Time: 18:30
 */
class DefaultConfig {
    companion object {
        fun get(): Config {

            val tablistConfiguration = TablistConfiguration(
                listOf("Proxy"),
                listOf(
                    "<dark_gray>                                                                        <dark_gray>",
                    "<aqua>SimpleCloud <dark_gray>» <gray>Simplify <white><italic>your</italic> <gray>network",
                    "<dark_aqua>Online <dark_gray>» <gray>%ONLINE_PLAYERS%<dark_gray>/<gray>%MAX_PLAYERS% <dark_gray>┃ <dark_aqua>Server <dark_gray>» <gray>%DISPLAYNAME%",
                    "<dark_gray>",
                ),
                listOf(
                    "<dark_gray>",
                    "<dark_aqua>Twitter <dark_gray>» <gray>@theSimpleCloud",
                    "<dark_aqua>Discord <dark_gray>» <gray>discord.gg/MPZs4h8",
                    "<dark_aqua>Powered by <dark_gray>» <gray>Venocix.de",
                    "<dark_gray>",
                )
            )

            val config = Config(
                CloudAPI.instance.getCloudServiceGroupManager().getProxyGroups()
                    .map { getDefaultProxyGroupConfiguration(it.getName()) },
                listOf(tablistConfiguration),
                "§cThis service is in maintenance",
                "§cThis service is full"
            )

            return config
        }

        fun getDefaultProxyGroupConfiguration(groupName: String): ProxyGroupConfiguration {
            val motdConfiguration = MotdConfiguration(
                listOf("<gradient:#00fcff:#0da1a3><bold>SimpleCloud</bold></gradient> <dark_gray>»<gray> Simplify your network <dark_gray>|<bold><#4595ff> 1.12<#545454> - <#4595ff>1.19</bold>"),
                listOf("<dark_gray>× <#178fff>Status<dark_gray>: <bold><#22cc22>Online</bold> <dark_gray>- <#ffffff>%PROXY%"),
                emptyList(),
                null
            )
            val maintenanceMotdConfiguration = MotdConfiguration(
                listOf("<gradient:#00fcff:#0da1a3><bold>SimpleCloud</bold></gradient> <dark_gray>»<gray> Simplify your network <dark_gray>|<bold><#4595ff> 1.12<#545454> - <#4595ff>1.19</bold>"),
                listOf("<dark_gray>× <#178fff>Status<dark_gray>: <bold><#c40000>Maintenance</bold> <dark_gray>- <white>%PROXY%"),
                emptyList(),
                "§4§oMaintenance"
            )

            return ProxyGroupConfiguration(
                groupName,
                mutableListOf("Fllip", "Wetterbericht"),
                motdConfiguration,
                maintenanceMotdConfiguration
            )
        }
    }
}