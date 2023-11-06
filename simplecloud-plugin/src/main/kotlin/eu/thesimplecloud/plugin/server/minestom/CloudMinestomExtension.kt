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

package eu.thesimplecloud.plugin.server.minestom

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.plugin.impl.player.CloudPlayerManagerMinestom
import eu.thesimplecloud.plugin.listener.CloudListener
import eu.thesimplecloud.plugin.server.ICloudServerPlugin
import eu.thesimplecloud.plugin.server.minestom.listener.MinestomListener
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.minestom.server.MinecraftServer
import net.minestom.server.extensions.Extension
import java.time.Duration
import kotlin.reflect.KClass

class CloudMinestomExtension : Extension(), ICloudServerPlugin {

    companion object {
        @JvmStatic
        lateinit var instance: CloudMinestomExtension
    }

    init {
        instance = this
    }

    override fun preInitialize() {
        CloudPlugin(this)
    }

    override fun initialize() {
        CloudPlugin.instance.onEnable()
        CloudAPI.instance.getEventManager().registerListener(CloudPlugin.instance, CloudListener())
        MinestomListener.register(MinecraftServer.getGlobalEventHandler())
        synchronizeOnlineCountTask()
    }

    override fun terminate() {
        CloudPlugin.instance.onDisable()
    }

    override fun getCloudPlayerManagerClass(): KClass<out ICloudPlayerManager> {
        return CloudPlayerManagerMinestom::class
    }

    override fun shutdown() {
        MinecraftServer.getServer().stop()
    }

    private fun synchronizeOnlineCountTask() {
        MinecraftServer.getSchedulerManager().buildTask {
            val service = CloudPlugin.instance.thisService()
            val onlinePlayers = MinecraftServer.getConnectionManager().onlinePlayers
            val onlinePlayerCount = onlinePlayers.size

            if (service.getOnlineCount() != onlinePlayerCount) {
                service.setOnlineCount(onlinePlayerCount)
                service.update()
            }
        }
            .delay(Duration.ofSeconds(30))
            .repeat(Duration.ofSeconds(30))
            .schedule()
    }

}