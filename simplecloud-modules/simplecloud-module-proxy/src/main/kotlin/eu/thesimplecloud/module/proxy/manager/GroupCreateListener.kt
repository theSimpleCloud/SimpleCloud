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

package eu.thesimplecloud.module.proxy.manager

import eu.thesimplecloud.api.event.group.CloudServiceGroupCreatedEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.module.proxy.config.Config
import eu.thesimplecloud.module.proxy.config.DefaultConfig

/**
 * Created by IntelliJ IDEA.
 * Date: 27.12.2020
 * Time: 18:00
 * @author Frederick Baier
 */
class GroupCreateListener(private val proxyModule: ProxyModule) : IListener {

    @CloudEventHandler
    fun handle(event: CloudServiceGroupCreatedEvent) {
        val proxyGroup = event.serviceGroup
        if (proxyGroup.getServiceType() != ServiceType.PROXY) return
        if (proxyModule.getProxyConfiguration(proxyGroup.getName()) != null) return

        val proxyGroupConfiguration = DefaultConfig.getDefaultProxyGroupConfiguration(proxyGroup.getName())
        val newConfig = Config(
                proxyModule.config.proxyGroupConfigurations.union(listOf(proxyGroupConfiguration)).toList(),
                proxyModule.config.tablistConfigurations,
                proxyModule.config.maintenanceKickMessage,
                proxyModule.config.fullProxyKickMessage
        )
        proxyModule.saveConfig(newConfig)
        proxyModule.loadConfig()

    }

}