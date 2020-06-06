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

package eu.thesimplecloud.module.sign.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.location.TemplateLocation
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.module.sign.lib.SignModuleConfig
import eu.thesimplecloud.plugin.extension.toCloudLocation
import org.bukkit.Bukkit
import org.bukkit.Location
import kotlin.math.min

class BukkitCloudSignManager {

    private val bukkitCloudSigns = ArrayList<BukkitCloudSign>()

    init {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(SpigotPluginMain.INSTANCE, {
            val signModuleConfig = try {
                SignModuleConfig.INSTANCE.obj
            } catch (e: Exception) {
                println("[SimpleCloud-Signs] WARNING: Module config not instantiated.")
                return@scheduleSyncRepeatingTask
            }
            unregisterRemovedSigns(signModuleConfig)
            registerNewSigns(signModuleConfig)

            this.bukkitCloudSigns.forEach { it.checkForExpiredService() }

            val allServerGroups = CloudAPI.instance.getCloudServiceGroupManager().getAllCachedObjects()
                    .filter { it.getServiceType() != ServiceType.PROXY }
            for (serviceGroup in allServerGroups) {
                val waitingServices = getWaitingServices(serviceGroup)
                if (waitingServices.isEmpty()) continue
                val waitingSigns = getWaitingSigns(serviceGroup)
                if (waitingSigns.isEmpty()) continue

                for (i in 0 until min(waitingServices.size, waitingSigns.size)) {
                    waitingSigns[i].currentServer = waitingServices[i]
                }
            }
            signModuleConfig.signLayouts.forEach { it.nextFrame() }
            this.bukkitCloudSigns.forEach { it.update() }
        }, 10, 10)

    }

    private fun getWaitingSigns(cloudServiceGroup: ICloudServiceGroup): List<BukkitCloudSign> {
        return this.bukkitCloudSigns
                .filter { it.cloudSign.forGroup == cloudServiceGroup.getName() }
                .filter { it.currentServer == null }
    }

    private fun getWaitingServices(cloudServiceGroup: ICloudServiceGroup): List<ICloudService> {
        return cloudServiceGroup.getAllServices()
                .filter { it.getState() == ServiceState.STARTING || it.getState() == ServiceState.VISIBLE }
                .sortedByDescending { it.getState() }
                .filter { getBukkitCloudSignsByServer(it) == null }
    }

    private fun unregisterRemovedSigns(signModuleConfig: SignModuleConfig) {
        val signsToRemove = this.bukkitCloudSigns.filter { signModuleConfig.getCloudSignByLocation(it.templateLocation) == null }
        signsToRemove.forEach { it.clearSign() }
        this.bukkitCloudSigns.removeAll(signsToRemove)
    }

    private fun registerNewSigns(signModuleConfig: SignModuleConfig) {
        val signsToRegister = signModuleConfig.cloudSigns.filter { getBukkitCloudSignByTemplateLocation(it.templateLocation) == null }
        for (cloudSign in signsToRegister) {
            this.bukkitCloudSigns.add(BukkitCloudSign(cloudSign))
        }
    }

    private fun getBukkitCloudSignByTemplateLocation(templateLocation: TemplateLocation): BukkitCloudSign? {
        return this.bukkitCloudSigns.firstOrNull { it.templateLocation == templateLocation }
    }

    fun getBukkitCloudSignByLocation(location: Location): BukkitCloudSign? {
        return getBukkitCloudSignByTemplateLocation(location.toCloudLocation().toTemplateLocation())
    }

    private fun getBukkitCloudSignsByServer(cloudService: ICloudService): BukkitCloudSign? {
        return this.bukkitCloudSigns.firstOrNull { it.currentServer == cloudService }
    }


}