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

import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin

/**
 * Created by IntelliJ IDEA.
 * Date: 11.10.2020
 * Time: 19:04
 * @author Frederick Baier
 */
class BukkitSignAPI(plugin: JavaPlugin) {

    val serviceViewManager: BukkitSignServiceViewManager = BukkitSignServiceViewManager(plugin)

    init {
        instance = this
    }

    fun getAllRegisteredSigns(): List<BukkitCloudSign> {
        return this.serviceViewManager.getAllGroupViewManagers().map { it.getServiceViewers() }.flatten()
    }

    fun getAllRegisteredSignsByGroup(group: ICloudServiceGroup): List<BukkitCloudSign> {
        return this.serviceViewManager.getGroupView(group).getServiceViewers()
    }

    fun getSignOnLocation(location: Location): BukkitCloudSign? {
        return getAllRegisteredSigns().firstOrNull { it.bukkitLocation == location }
    }

    fun isSignOnLocation(location: Location): Boolean {
        return getSignOnLocation(location) != null
    }


    companion object {
        lateinit var instance: BukkitSignAPI
            private set
    }

}