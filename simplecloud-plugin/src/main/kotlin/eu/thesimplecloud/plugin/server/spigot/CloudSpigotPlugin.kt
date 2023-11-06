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

package eu.thesimplecloud.plugin.server.spigot

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.plugin.impl.player.CloudPlayerManagerSpigot
import eu.thesimplecloud.plugin.listener.CloudListener
import eu.thesimplecloud.plugin.server.ICloudServerPlugin
import eu.thesimplecloud.plugin.server.spigot.listener.ReloadCommandBlocker
import eu.thesimplecloud.plugin.server.spigot.listener.SpigotListener
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import kotlin.reflect.KClass

class CloudSpigotPlugin : JavaPlugin(), ICloudServerPlugin {

    companion object {
        @JvmStatic
        lateinit var instance: CloudSpigotPlugin
    }

    init {
        instance = this
    }

    override fun onLoad() {
        CloudPlugin(this)
    }

    override fun onEnable() {
        CloudPlugin.instance.onEnable()
        CloudAPI.instance.getEventManager().registerListener(CloudPlugin.instance, CloudListener())
        server.pluginManager.registerEvents(SpigotListener(), this)
        server.pluginManager.registerEvents(ReloadCommandBlocker(), this)
        synchronizeOnlineCountTask()
    }

    override fun onBeforeFirstUpdate() {
        //Service will be updated after this function was called
        val motd = Bukkit.getServer().motd
        CloudPlugin.instance.thisService().setMOTD(motd)
    }

    override fun onDisable() {
        CloudPlugin.instance.onDisable()
    }

    override fun shutdown() {
        Bukkit.getServer().shutdown()
    }

    override fun getCloudPlayerManagerClass(): KClass<out ICloudPlayerManager> {
        return CloudPlayerManagerSpigot::class
    }

    private fun synchronizeOnlineCountTask() {
        object : BukkitRunnable() {
            override fun run() {
                val service = CloudPlugin.instance.thisService()
                if (service.getOnlineCount() != server.onlinePlayers.size) {
                    service.setOnlineCount(server.onlinePlayers.size)
                    service.update()
                }
            }
        }.runTaskTimerAsynchronously(this, 20 * 30, 20 * 30)
    }

}