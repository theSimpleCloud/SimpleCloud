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

package eu.simplecloud.extension.impl.player

import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.api.exception.NoSuchWorldException
import eu.thesimplecloud.api.location.ServiceLocation
import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.api.network.packets.player.PacketIOGetPlayerLocation
import eu.thesimplecloud.api.network.packets.player.PacketIOTeleportPlayer
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.plugin.impl.player.AbstractCloudPlayerManagerServer
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.05.2020
 * Time: 21:58
 */
class CloudPlayerManagerMinestom : AbstractCloudPlayerManagerServer() {

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: SimpleLocation): ICommunicationPromise<Unit> {
        if (CloudPlugin.instance.thisServiceName != cloudPlayer.getConnectedServerName()) {
            return CloudPlugin.instance.connectionToManager.sendUnitQuery(PacketIOTeleportPlayer(cloudPlayer, location))
        }

        val minestomPlayer = getPlayerByCloudPlayer(cloudPlayer)
        minestomPlayer
            ?: return CommunicationPromise.failed(NoSuchPlayerException("Unable to find the player on the server service"))
        val minestomLocation = getLocationBySimpleLocation(location)
        val instance = MinecraftServer.getInstanceManager().getInstance(UUID.fromString(location.worldName))
            ?: return CommunicationPromise.failed(NoSuchWorldException("Unable to find world: ${location.worldName}"))

        minestomPlayer.setInstance(instance, minestomLocation)
        return CommunicationPromise.of(Unit)
    }

    override fun getLocationOfPlayer(cloudPlayer: ICloudPlayer): ICommunicationPromise<ServiceLocation> {
        if (CloudPlugin.instance.thisServiceName != cloudPlayer.getConnectedServerName()) {
            return CloudPlugin.instance.connectionToManager.sendQuery(PacketIOGetPlayerLocation(cloudPlayer))
        }

        val minestomPlayer = getPlayerByCloudPlayer(cloudPlayer)
        minestomPlayer ?: return CommunicationPromise.failed(NoSuchPlayerException("Unable to find bukkit player"))

        val playerLocation = minestomPlayer.position
        val instance = minestomPlayer.instance
            ?: return CommunicationPromise.failed(NoSuchWorldException("The world the player is on is null"))

        return CommunicationPromise.of(
            ServiceLocation(
                CloudPlugin.instance.thisService(),
                instance.uniqueId.toString(),
                playerLocation.x(),
                playerLocation.y(),
                playerLocation.z(),
                playerLocation.yaw(),
                playerLocation.pitch()
            )
        )
    }

    override fun getPlayerPing(cloudPlayer: ICloudPlayer): ICommunicationPromise<Int> {
        //TODO: Check if latency is the same as ping
        return CommunicationPromise.of(getPlayerByCloudPlayer(cloudPlayer)?.latency ?: -1)
    }

    private fun getLocationBySimpleLocation(simpleLocation: SimpleLocation): Pos {
        return Pos(
            simpleLocation.x,
            simpleLocation.y,
            simpleLocation.z,
            simpleLocation.yaw,
            simpleLocation.pitch
        )
    }

    private fun getPlayerByCloudPlayer(cloudPlayer: ICloudPlayer): Player? {
        return MinecraftServer.getConnectionManager().getPlayer(cloudPlayer.getUniqueId())
    }

}