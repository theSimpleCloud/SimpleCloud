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

package eu.thesimplecloud.base.manager.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.base.manager.network.packets.player.PacketOutGetPlayerOnlineStatus
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.launcher.startup.Launcher
import java.util.concurrent.TimeUnit

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 10.04.2020
 * Time: 21:35
 */
class PlayerUnregisterScheduler {

    fun startScheduler() {
        Launcher.instance.scheduler.scheduleAtFixedRate({
            CloudAPI.instance.getCloudPlayerManager().getAllCachedObjects().forEach { player ->

                checkPlayerOnlineStatus(player).then {
                    if (!it) {
                        CloudAPI.instance.getCloudPlayerManager().delete(player)
                    }
                }

            }
        }, 30L, 30, TimeUnit.SECONDS)
    }

    private fun checkPlayerOnlineStatus(cloudPlayer: ICloudPlayer): ICommunicationPromise<Boolean> {
        val connectedProxy = cloudPlayer.getConnectedProxy() ?: return CommunicationPromise.of(false)

        val client = Manager.instance.communicationServer.getClientManager().getClientByClientValue(connectedProxy)
                ?: return CommunicationPromise.of(false)

        return client.sendQuery(PacketOutGetPlayerOnlineStatus(cloudPlayer.getUniqueId()))
    }

}