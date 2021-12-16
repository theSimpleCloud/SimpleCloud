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
                    "§8                                                                        §8",
                    "§bSimpleCloud §8» §7Simplify §f§oyour §7network",
                    "§3Online §8» §7%ONLINE_PLAYERS%§8/§7%MAX_PLAYERS% §8┃ §3Server §8» §7%SERVER%",
                    "§8",
                ),
                listOf(
                    "§8",
                    "§3Twitter §8» §7@theSimpleCloud",
                    "§3Discord §8» §7discord.gg/MPZs4h8",
                    "§3Powered by §8» §7Venocix.de",
                    "§8",
                ))

            val config = Config(
                emptyList(),
                listOf(tablistConfiguration),
                "§cThis service is in maintenance",
                "§cThis service is full")

            return config
        }

        fun getDefaultProxyGroupConfiguration(groupName: String): ProxyGroupConfiguration {
            val motdConfiguration = MotdConfiguration(
                listOf("<gradient:#00fcff:#0da1a3><bold>SimpleCloud</bold></gradient> §8»§7 Simplify your network §8|<bold><#4595ff> 1.12<#545454> - <#4595ff>1.18</bold>"),
                listOf("§8× <#178fff>Status§8: <bold><#22cc22>Online</bold> §8- <#ffffff>%PROXY%"),
                emptyList(),
                null)
            val maintenanceMotdConfiguration = MotdConfiguration(
                listOf("<gradient:#00fcff:#0da1a3><bold>SimpleCloud</bold></gradient> §8»§7 Simplify your network §8|<bold><#4595ff> 1.12<#545454> - <#4595ff>1.18</bold>"),
                listOf("§8× <#178fff>Status§8: <bold><#c40000>Maintenance</bold> §8- §f%PROXY%"),
                emptyList(),
                "§4§oMaintenance")

            return ProxyGroupConfiguration(
                groupName,
                mutableListOf("Fllip", "Wetterbericht"),
                motdConfiguration,
                maintenanceMotdConfiguration)
        }
    }
}