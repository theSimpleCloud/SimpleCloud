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

package eu.thesimplecloud.base.manager.listener

import eu.thesimplecloud.api.event.player.CloudPlayerLoginEvent
import eu.thesimplecloud.api.event.player.CloudPlayerUnregisteredEvent
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.event.module.ModuleUnloadedEvent
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher

class CloudListener : IListener {

    @CloudEventHandler
    fun on(event: CloudServiceUnregisteredEvent) {
        Launcher.instance.consoleSender.sendMessage("manager.service.stopped", "Service %SERVICE%", event.cloudService.getName(), " was stopped.")
        val activeScreen = Launcher.instance.screenManager.getActiveScreen()
        activeScreen?.let {
            if (activeScreen.getName().equals(event.cloudService.getName(), true)) {
                Launcher.instance.screenManager.leaveActiveScreen()
            }
        }
        Launcher.instance.screenManager.unregisterScreen(event.cloudService.getName())
    }

    @CloudEventHandler
    fun on(event: ModuleUnloadedEvent) {
        Manager.instance.packetRegistry.unregisterAllPackets(event.module.cloudModule)
    }

    @CloudEventHandler
    fun on(event: CloudPlayerLoginEvent) {
        event.getCloudPlayer().then {
            Launcher.instance.consoleSender
                    .sendMessage("manager.player.connected", "Player %NAME%", it.getName(), "(%UUID% ", it.getUniqueId().toString(),
                            "/ %IP%", it.getPlayerConnection().getAddress().getHostname(), ") connected.")
        }

    }

    @CloudEventHandler
    fun on(event: CloudPlayerUnregisteredEvent) {
        val player = event.cloudPlayer

        Launcher.instance.consoleSender
                .sendMessage("manager.player.disconnected", "Player %NAME%", player.getName(), "(%UUID% ", player.getUniqueId().toString(),
                        "/ %IP%", player.getPlayerConnection().getAddress().getHostname(), ") disconnected.")

    }

}