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

package eu.thesimplecloud.module.sign.service.spigot

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.module.serviceselection.api.ServiceViewGroupManager
import eu.thesimplecloud.module.sign.lib.sign.CloudSign
import eu.thesimplecloud.module.sign.service.lib.AbstractCloudSign
import eu.thesimplecloud.module.sign.service.lib.AbstractSignManager
import eu.thesimplecloud.module.sign.service.spigot.event.BukkitCloudSignAddedEvent
import eu.thesimplecloud.module.sign.service.spigot.event.BukkitCloudSignRemovedEvent
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

/**
 * Created by IntelliJ IDEA.
 * Date: 04/02/2021
 * Time: 14:33
 * @author Frederick Baier
 */
class BukkitSignManager(
    bukkitSignServiceViewManager: BukkitSignServiceViewManager,
    private val plugin: JavaPlugin
) : AbstractSignManager(bukkitSignServiceViewManager) {

    init {
        setupRegisteredGroups()
    }

    @Synchronized
    override fun unregisterSign(
        abstractCloudSign: AbstractCloudSign,
        groupView: ServiceViewGroupManager<AbstractCloudSign>
    ) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            groupView.removeServiceViewer(abstractCloudSign)
            CloudAPI.instance.getEventManager().call(BukkitCloudSignRemovedEvent(abstractCloudSign as BukkitCloudSign))
        })
    }

    @Synchronized
    override fun registerCloudSign(cloudSign: CloudSign) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            val groupView = this.abstractServiceViewManager.getGroupView(cloudSign.getGroup())
            if (isSignRegistered(cloudSign, groupView.getServiceViewers())) return@Runnable

            val bukkitCloudSign = BukkitCloudSign(cloudSign)
            groupView.addServiceViewers(bukkitCloudSign)
            CloudAPI.instance.getEventManager().call(BukkitCloudSignAddedEvent(bukkitCloudSign))
        })
    }
}