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

package eu.thesimplecloud.module.cloudflare

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.cloudflare.config.CloudFlareConfigLoader
import eu.thesimplecloud.module.cloudflare.listener.CloudFlareSingleGroupListener

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.02.2020
 * Time: 17:31
 */
class CloudFlareModule : ICloudModule {

    private val cloudFlareConfigs = CloudFlareConfigLoader().loadAll()
    private val cloudFlareHelpers = cloudFlareConfigs.map { CloudFlareHelper(it) }

    override fun onEnable() {
        cloudFlareHelpers.forEach { cloudFlareHelper ->
            val config = cloudFlareHelper.config
            if (config.email == "me@example.com") return@forEach
            cloudFlareHelper.isCloudFlareConfiguredCorrectly().thenAccept {
                if (it) {
                    registerAllRunningServices(cloudFlareHelper)
                    CloudAPI.instance.getEventManager()
                        .registerListener(this, CloudFlareSingleGroupListener(cloudFlareHelper))
                    cloudFlareHelper.createARecordIfNotExist()
                    Launcher.instance.logger.success("The CloudFlare Module is active for group ${config.targetProxyGroup}!")
                } else {
                    Launcher.instance.logger.warning("The CloudFlare Module is not configured correctly for group ${config.targetProxyGroup}!")

                }
            }
        }
    }

    private fun registerAllRunningServices(cloudFlareHelper: CloudFlareHelper) {
        val targetProxyGroup = cloudFlareHelper.config.targetProxyGroup
        val group = CloudAPI.instance.getCloudServiceGroupManager().getProxyGroupByName(targetProxyGroup) ?: return
        group.getAllServices().filter { it.isOnline() }.forEach { cloudFlareHelper.createSRVRecord(it) }
    }

    override fun onDisable() {
        cloudFlareHelpers.forEach { it.deleteAllSRVRecordsAndWait() }
    }
}