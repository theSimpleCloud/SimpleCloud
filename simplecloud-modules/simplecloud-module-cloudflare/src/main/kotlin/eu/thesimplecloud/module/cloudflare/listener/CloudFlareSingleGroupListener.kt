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

package eu.thesimplecloud.module.cloudflare.listener

import eu.thesimplecloud.api.event.service.CloudServiceStartedEvent
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.module.cloudflare.config.proxy.ProxyConfig
import eu.thesimplecloud.module.cloudflare.domain.CloudFlareDomainHelper

/**
 * Created by IntelliJ IDEA.
 * Date: 14.12.2020
 * Time: 17:47
 * @author Frederick Baier
 */
class CloudFlareSingleGroupListener(
    private val cloudFlareHelper: CloudFlareDomainHelper,
    private val allProxyConfigs: Collection<ProxyConfig>
) : IListener {

    private val config = cloudFlareHelper.config

    @CloudEventHandler
    fun handleServiceStarted(event: CloudServiceStartedEvent) {
        val cloudService = event.cloudService
        val proxyConfig = getProxyConfigByService(cloudService) ?: return
        if (proxyConfig.domain != this.config.domain) return
        if (cloudService.isProxy()) {
            cloudFlareHelper.createSRVRecord(cloudService, proxyConfig)
        }
    }

    @CloudEventHandler
    fun handleServiceStarted(event: CloudServiceUnregisteredEvent) {
        val cloudService = event.cloudService
        if (cloudService.isProxy()) {
            cloudFlareHelper.deleteSRVRecord(cloudService)
        }
    }

    private fun getProxyConfigByService(service: ICloudService): ProxyConfig? {
        return this.allProxyConfigs.firstOrNull { it.targetProxyGroup == service.getGroupName() }
    }

}