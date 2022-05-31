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

package eu.thesimplecloud.api.player.connection

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.player.CloudPlayerServerConnectedEvent
import eu.thesimplecloud.api.listenerextension.cloudListener
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.player.PlayerServerConnectState
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

class ConnectionResponse(val playerUniqueId: UUID, val alreadyConnected: Boolean) {

    fun getCloudPlayer(): ICloudPlayer {
        return CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)
            ?: throw IllegalStateException("Unable to find player by uuid $playerUniqueId")
    }

    fun createConnectedPromise(): ICommunicationPromise<CloudPlayerServerConnectedEvent> {
        val cloudPlayer = this.getCloudPlayer()
        if (alreadyConnected || cloudPlayer.getServerConnectState() == PlayerServerConnectState.CONNECTED)
            return CommunicationPromise.of(
                CloudPlayerServerConnectedEvent(
                    cloudPlayer,
                    cloudPlayer.getConnectedServer()!!
                )
            )
        return cloudListener<CloudPlayerServerConnectedEvent>()
            .addCondition { it.cloudPlayer === cloudPlayer }
            .toPromise()
    }

}