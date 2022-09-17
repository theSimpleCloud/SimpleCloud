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

package eu.thesimplecloud.plugin.server.limbo

import com.loohp.limbo.plugins.LimboPlugin
import com.loohp.limbo.scheduler.LimboTask
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.plugin.impl.player.CloudPlayerManagerLimbo
import eu.thesimplecloud.plugin.listener.CloudListener
import eu.thesimplecloud.plugin.server.ICloudServerPlugin
import eu.thesimplecloud.plugin.server.limbo.listener.LimboListener
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.kyori.adventure.text.TextComponent
import kotlin.reflect.KClass

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 17.09.22
 * Time: 20:41
 */
class CloudLimboPlugin : LimboPlugin(), ICloudServerPlugin {

    companion object {
        @JvmStatic
        lateinit var instance: CloudLimboPlugin
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
        server.eventsManager.registerEvents(this, LimboListener())
        synchronizeOnlineCountTask()
    }

    override fun onBeforeFirstUpdate() {
        //Service will be updated after this function was called
        val motd = server.serverProperties.motd as? TextComponent?: return
        CloudPlugin.instance.thisService().setMOTD(motd.content())
    }

    override fun onDisable() {
        CloudPlugin.instance.onDisable()
    }

    override fun shutdown() {
        server.stopServer()
    }

    override fun getCloudPlayerManagerClass(): KClass<out ICloudPlayerManager> {
        return CloudPlayerManagerLimbo::class
    }

    private fun synchronizeOnlineCountTask() {
        server.scheduler.runTaskTimerAsync(this, {
            val service = CloudPlugin.instance.thisService()
            if (service.getOnlineCount() != server.players.size) {
                service.setOnlineCount(server.players.size)
                service.update()
            }
        }, 20 * 30, 20 * 30)
    }

}