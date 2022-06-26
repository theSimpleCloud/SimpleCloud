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

package eu.thesimplecloud.module.cloudflare

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.cloudflare.config.domain.DomainConfigLoader
import eu.thesimplecloud.module.cloudflare.config.proxy.ProxyConfig
import eu.thesimplecloud.module.cloudflare.config.proxy.ProxyConfigLoader
import eu.thesimplecloud.module.cloudflare.domain.CloudFlareDomainHelper
import eu.thesimplecloud.module.cloudflare.listener.CloudFlareSingleGroupListener

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.02.2020
 * Time: 17:31
 */
class CloudFlareModule : ICloudModule {

    private val domainConfigs = DomainConfigLoader().loadAll()
    private val proxyConfigs = ProxyConfigLoader().loadAll()
    private val domainHelpers = domainConfigs.map { CloudFlareDomainHelper(it) }

    override fun onEnable() {
        proxyConfigs.forEach {
            Launcher.instance.consoleSender.sendProperty("module.cloudflare.proxy-config.loaded", it.targetProxyGroup)
        }

        domainHelpers.forEach { cloudFlareHelper ->
            val config = cloudFlareHelper.config
            if (config.email == "me@example.com") return@forEach
            cloudFlareHelper.isCloudFlareConfiguredCorrectly().thenAccept {
                if (it) {
                    registerAllRunningServices(cloudFlareHelper)
                    CloudAPI.instance.getEventManager()
                        .registerListener(this, CloudFlareSingleGroupListener(cloudFlareHelper, proxyConfigs))
                    cloudFlareHelper.createARecordsForWrappersIfNotExist(
                        CloudAPI.instance.getWrapperManager().getAllCachedObjects()
                    )
                    Launcher.instance.consoleSender.sendProperty("module.cloudflare.domain.active", config.domain)
                } else {
                    Launcher.instance.consoleSender.sendProperty("module.cloudflare.domain.invalid", config.domain)
                }
            }
        }
    }

    private fun registerAllRunningServices(cloudFlareHelper: CloudFlareDomainHelper) {
        val proxyConfigs = getAllProxyConfigsByDomain(cloudFlareHelper.config.domain)
        proxyConfigs.forEach { registerAllServicesByProxyConfig(cloudFlareHelper, it) }
    }

    private fun registerAllServicesByProxyConfig(cloudFlareHelper: CloudFlareDomainHelper, proxyConfig: ProxyConfig) {
        val group =
            CloudAPI.instance.getCloudServiceGroupManager().getProxyGroupByName(proxyConfig.targetProxyGroup) ?: return
        group.getAllServices().filter { it.isOnline() }.forEach { cloudFlareHelper.createSRVRecord(it, proxyConfig) }
    }

    private fun getAllProxyConfigsByDomain(domain: String): List<ProxyConfig> {
        return this.proxyConfigs.filter { it.domain == domain }
    }

    override fun onDisable() {
        domainHelpers.forEach { it.deleteAllSRVRecordsAndWait() }
    }
}