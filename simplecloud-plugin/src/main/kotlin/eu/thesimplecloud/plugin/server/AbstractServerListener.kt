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

package eu.thesimplecloud.plugin.server

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.player.CloudPlayerChatEvent
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.Bukkit
import java.util.*

abstract class AbstractServerListener {

    protected fun checkAddress(
        uniqueId: UUID,
        hostAddress: String,
        disableHostCheck: Boolean = false,
        kickCallback: (String) -> Unit,
    ) {
        if (!disableHostCheck) {
            if (hostAddress != "127.0.0.1" && !CloudAPI.instance.getWrapperManager().getAllCachedObjects()
                    .any { it.getHost() == hostAddress }
            ) {
                println("Unknown address $hostAddress")
                kickCallback(ServerMessages.UNKNOWN_ADRESS)
                return
            }
        }

        if (CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(uniqueId) == null) {
            kickCallback(ServerMessages.NOT_REGISTERED)
        }
    }

    protected fun callChatEvent(uniqueId: UUID, message: String) {
        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager()
            .getCachedCloudPlayer(uniqueId) ?: return
        val playerChatEvent = CloudPlayerChatEvent(cloudPlayer, message, CloudPlugin.instance.thisService())
        CloudAPI.instance.getEventManager().call(playerChatEvent)
    }

    protected fun handleDisconnect(uniqueId: UUID) {
        val playerManager = CloudAPI.instance.getCloudPlayerManager()
        val cloudPlayer = playerManager.getCachedCloudPlayer(uniqueId)

        if (cloudPlayer != null && !cloudPlayer.isUpdatesEnabled()) {
            playerManager.delete(cloudPlayer)
        }
        updateCurrentOnlineCountTo(Bukkit.getOnlinePlayers().size - 1)
    }

    protected fun updateCurrentOnlineCountTo(count: Int) {
        val thisService = CloudPlugin.instance.thisService()
        thisService.setOnlineCount(count)
        thisService.update()
    }

}