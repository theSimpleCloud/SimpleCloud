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

package eu.thesimplecloud.plugin.proxy.bungee.listener

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.player.CloudPlayerCommandExecuteEvent
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import eu.thesimplecloud.plugin.network.packets.PacketOutPlayerExecuteCommand
import eu.thesimplecloud.plugin.proxy.ProxyEventHandler
import eu.thesimplecloud.plugin.proxy.bungee.CloudBungeePlugin
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.event.TabCompleteEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class IngameCommandListener : Listener {

    @EventHandler
    fun on(event: ChatEvent) {
        if (event.sender !is ProxiedPlayer) return
        val player = event.sender as ProxiedPlayer
        if (event.isCommand) {
            val rawCommand = event.message.replaceFirst("/", "")
            val commandStart = rawCommand.split(" ")[0]
            if (CloudBungeePlugin.instance.synchronizedIngameCommandsProperty.getValue()
                    .contains(commandStart.toLowerCase())
            ) {
                CloudPlugin.instance.connectionToManager.sendUnitQuery(
                    PacketOutPlayerExecuteCommand(
                        player.getCloudPlayer(),
                        rawCommand
                    )
                )
                event.isCancelled = true
            }
            CloudAPI.instance.getEventManager()
                .call(CloudPlayerCommandExecuteEvent(player.uniqueId, player.name, rawCommand))
        }
    }

    @EventHandler
    fun on(event: TabCompleteEvent) {
        val player = event.sender as? ProxiedPlayer ?: return

        event.suggestions.addAll(ProxyEventHandler.handleTabComplete(player.uniqueId, event.cursor))
    }

}