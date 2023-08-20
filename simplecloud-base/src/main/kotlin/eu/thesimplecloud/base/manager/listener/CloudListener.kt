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

package eu.thesimplecloud.base.manager.listener

import eu.thesimplecloud.api.event.player.CloudPlayerLoginEvent
import eu.thesimplecloud.api.event.player.CloudPlayerUnregisteredEvent
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.event.module.ModuleUnloadedEvent
import eu.thesimplecloud.launcher.screens.session.ScreenSession
import eu.thesimplecloud.launcher.startup.Launcher

class CloudListener : IListener {

    @CloudEventHandler
    fun on(event: CloudServiceUnregisteredEvent) {
        Launcher.instance.consoleSender.sendProperty("manager.service.stopped", event.cloudService.getName())

        val activeSession = Launcher.instance.screenManager.getActiveScreenSession()
        activeSession?.let {
            if (activeSession.screen.getName().equals(event.cloudService.getName(), true)) {
                if (activeSession.screenCloseBehaviour == ScreenSession.ScreenCloseBehaviour.CLOSE)
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
                .sendProperty(
                    "manager.player.connected",
                    it.getName(),
                    it.getUniqueId().toString(),
                    it.getPlayerConnection().getAddress().getHostname(),
                    it.getConnectedProxyName()
                )
        }

    }

    @CloudEventHandler
    fun on(event: CloudPlayerUnregisteredEvent) {
        val player = event.cloudPlayer

        Launcher.instance.consoleSender
            .sendProperty(
                "manager.player.disconnected", player.getName(), player.getUniqueId().toString(),
                player.getPlayerConnection().getAddress().getHostname()
            )

    }

}