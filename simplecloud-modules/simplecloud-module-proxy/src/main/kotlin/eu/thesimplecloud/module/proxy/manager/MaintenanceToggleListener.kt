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

package eu.thesimplecloud.module.proxy.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.group.CloudServiceGroupUpdatedEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.module.proxy.config.Config
import eu.thesimplecloud.module.proxy.config.ProxyGroupConfiguration
import eu.thesimplecloud.module.proxy.extensions.mapToLowerCase
import eu.thesimplecloud.module.proxy.service.ProxyHandler

/**
 * Created by IntelliJ IDEA.
 * Date: 21.05.2021
 * Time: 18:51
 * @author Frederick Baier
 */
class MaintenanceToggleListener : IListener {

    private val configHolder = ProxyHandler.configHolder

    @CloudEventHandler
    fun on(event: CloudServiceGroupUpdatedEvent) {
        val serviceGroup = event.serviceGroup
        if (serviceGroup.isInMaintenance() && serviceGroup.getServiceType() == ServiceType.PROXY) {
            kickUnallowedPlayers(serviceGroup)
        }
    }

    private fun kickUnallowedPlayers(serviceGroup: ICloudServiceGroup) {
        val allPlayers = CloudAPI.instance.getCloudPlayerManager().getAllCachedObjects()
        val playersOnline = allPlayers.filter { it.getConnectedProxy()?.getServiceGroup() == serviceGroup }
        playersOnline.forEach { kickIfPermissionNotGrantedAndNotOnWhitelist(it, serviceGroup) }
    }

    private fun kickIfPermissionNotGrantedAndNotOnWhitelist(player: ICloudPlayer, serviceGroup: ICloudServiceGroup) {
        val hasPermissionPromise = isJoinPermissionGranted(player)
        hasPermissionPromise.then { hasMaintenancePermission ->
            if (!hasMaintenancePermission && !isPlayerOnWhitelist(player, serviceGroup))
                kickPlayerDueToMaintenance(player)
        }
    }

    private fun kickPlayerDueToMaintenance(player: ICloudPlayer) {
        val config = getConfig()
        player.kick(config.maintenanceKickMessage)
    }

    private fun isPlayerOnWhitelist(player: ICloudPlayer, serviceGroup: ICloudServiceGroup): Boolean {
        val proxyConfig =
            getProxyConfigurations().firstOrNull { it.proxyGroup == serviceGroup.getName() } ?: return false
        return proxyConfig.whitelist.mapToLowerCase().contains(player.getName().toLowerCase())
    }

    private fun isJoinPermissionGranted(player: ICloudPlayer): ICommunicationPromise<Boolean> {
        return player.hasPermission(ProxyHandler.JOIN_MAINTENANCE_PERMISSION)
    }

    private fun getProxyConfigurations(): List<ProxyGroupConfiguration> {
        return getConfig().proxyGroupConfigurations
    }

    private fun getConfig(): Config {
        return this.configHolder.getValue()
    }

}