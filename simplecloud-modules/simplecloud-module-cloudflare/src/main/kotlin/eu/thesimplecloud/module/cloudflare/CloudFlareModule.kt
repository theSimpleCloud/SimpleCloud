/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
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
import eu.thesimplecloud.module.cloudflare.api.CloudFlareAPI
import eu.thesimplecloud.module.cloudflare.config.ConfigManager

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.02.2020
 * Time: 17:31
 */
class CloudFlareModule: ICloudModule {

    val configManager = ConfigManager()
    val config = configManager.getConfig()
    val cloudFlareAPI = CloudFlareAPI(config)

    override fun onEnable() {
        CloudAPI.instance.getEventManager().registerListener(this, CloudListener(this))

        cloudFlareAPI.createForAlreadyStartedServices()
    }

    override fun onDisable() {
    }
}