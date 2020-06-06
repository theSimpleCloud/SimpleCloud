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

package eu.thesimplecloud.module.notify

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.service.CloudServiceRegisteredEvent
import eu.thesimplecloud.api.event.service.CloudServiceStartedEvent
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.api.service.ICloudService

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 13.04.2020
 * Time: 17:03
 */
class CloudListener(val module: NotifyModule) : IListener {

    private val permission = "cloud.module.notify.messages"

    @CloudEventHandler
    fun on(event: CloudServiceRegisteredEvent) {
        sendMessage(event.cloudService, module.config.serviceStartingMessage, false)
    }

    @CloudEventHandler
    fun on(event: CloudServiceStartedEvent) {
        sendMessage(event.cloudService, module.config.serviceStartedMessage, true)
    }

    @CloudEventHandler
    fun on(event: CloudServiceUnregisteredEvent) {
        sendMessage(event.cloudService, module.config.serviceStoppedMessage, false)
    }

    private fun sendMessage(service: ICloudService, message: String, addClick: Boolean) {
        val serviceName = service.getName()
        val replacedMessage = message.replace("%SERVICE%", serviceName)

        val cloudText = CloudText(replacedMessage)
        if (addClick) {
            cloudText.addHover(module.config.hoverMessage)
            cloudText.addClickEvent(CloudText.ClickEventType.RUN_COMMAND, "/server " + service.getName())
        }

        CloudAPI.instance.getCloudPlayerManager().getAllCachedObjects().forEach { cloudPlayer ->
            cloudPlayer.hasPermission(permission).then {
                if (it) {
                    cloudPlayer.sendMessage(cloudText)
                }
            }
        }
    }

}