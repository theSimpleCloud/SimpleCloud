/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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

package eu.thesimplecloud.module.prefix.service.spigot

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.module.prefix.service.command.ChatTabCommand
import eu.thesimplecloud.module.prefix.service.spigot.configuration.DelayConfiguration
import eu.thesimplecloud.module.prefix.service.spigot.listener.ChatListener
import eu.thesimplecloud.module.prefix.service.spigot.listener.CloudListener
import eu.thesimplecloud.module.prefix.service.spigot.listener.JoinListener
import eu.thesimplecloud.module.prefix.service.tablist.TablistHelper
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 19.12.2020
 * Time: 13:34
 */
class BukkitPluginMain : JavaPlugin() {

    val delayConfiguration: DelayConfiguration = DelayConfiguration()

    override fun onEnable() {
        TablistHelper.load()

        getCommand("chat-tab")?.setExecutor(ChatTabCommand(this))

        Bukkit.getPluginManager().registerEvents(JoinListener(this), this)
        Bukkit.getPluginManager().registerEvents(ChatListener(), this)

        CloudAPI.instance.getEventManager()
            .registerListener(CloudAPI.instance.getThisSidesCloudModule(), CloudListener())
    }

}