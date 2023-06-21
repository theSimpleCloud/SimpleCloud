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

package eu.thesimplecloud.module.sign.service

import eu.thesimplecloud.module.serviceselection.api.ServiceViewManager
import eu.thesimplecloud.module.sign.lib.SignModuleConfig
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin

/**
 * Created by IntelliJ IDEA.
 * Date: 11.10.2020
 * Time: 18:53
 * @author Frederick Baier
 */
class SignServiceViewManager(plugin: JavaPlugin) : ServiceViewManager<BukkitCloudSign>(
    plugin,
    SignModuleConfig.getConfig().cloudSignSettingsContainer.updateSignDelay
) {

    override fun performUpdate() {
        super.performUpdate()
        val config = SignModuleConfig.getConfig()
        val signLayoutContainer = config.signLayoutContainer
        signLayoutContainer.getAllLayouts().forEach { it.nextFrame() }
    }

    fun getBukkitCloudSignByLocation(location: Location): BukkitCloudSign? {
        val registeredSigns = this.getAllGroupViewManagers().map { it.getServiceViewers() }.flatten()
        return registeredSigns.firstOrNull { it.location == location }
    }

}