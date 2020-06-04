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
            val motdConfiguration = MotdConfiguration(
                    listOf("§bSimpleCloud §8» §7Simplify §f§oyour §7network §8[§f1.8-1.15§8]"),
                    listOf("§3Status §8» §aOnline"),
                    emptyList(),
                    null)
            val maintenanceMotdConfiguration = MotdConfiguration(
                    listOf("§bSimpleCloud §8» §7Simplify §f§oyour §7network §8[§f1.8-1.15§8]"),
                    listOf("§3Status §8» §cMaintenance"),
                    emptyList(),
                    "§8» §c§oMaintenance")
            val proxyGroupConfiguration = ProxyGroupConfiguration(
                    "Proxy",
                    mutableListOf("Fllip", "Wetterbericht"),
                    motdConfiguration,
                    maintenanceMotdConfiguration)

            val tablistConfiguration = TablistConfiguration(
                    listOf("Proxy"),
                    listOf(

                            "§8                                                                        §8\n" +
                                    "§bSimpleCloud §8» §7Simplify §f§oyour §7network\n" +
                                    "§3Online §8» §7%ONLINE_PLAYERS%§8/§7%MAX_PLAYERS% §8┃ §3Server §8» §7%SERVER%\n"
                    ),
                    listOf(
                            "§8\n" +
                                    "§3Twitter §8» §7@theSimpleCloud\n" +
                                    "§3Discord §8» §7discord.gg/MPZs4h8\n"
                    ),
                    1)

            val config = Config(
                    listOf(proxyGroupConfiguration),
                    listOf(tablistConfiguration),
                    "§cThis service is in maintenance",
                    "§cThis service is full")

            return config
        }
    }
}