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

package eu.thesimplecloud.plugin.impl.player

import eu.thesimplecloud.api.network.packets.player.PacketIOGetAllOnlinePlayers
import eu.thesimplecloud.api.network.packets.player.PacketIOGetCloudPlayer
import eu.thesimplecloud.api.network.packets.player.PacketIOGetOfflinePlayer
import eu.thesimplecloud.api.network.packets.player.PacketIOSetCloudPlayerUpdates
import eu.thesimplecloud.api.network.packets.sync.cachelist.PacketIOUpdateCacheObject
import eu.thesimplecloud.api.player.AbstractCloudPlayerManager
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.SimpleCloudPlayer
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.plugin.startup.CloudPlugin
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.05.2020
 * Time: 21:52
 */
abstract class AbstractServiceCloudPlayerManager : AbstractCloudPlayerManager() {

    override fun update(value: ICloudPlayer, fromPacket: Boolean, isCalledFromDelete: Boolean) {
        super.update(value, fromPacket, isCalledFromDelete)
        if (!fromPacket)
            CloudPlugin.instance.communicationClient.sendUnitQuery(
                    PacketIOUpdateCacheObject(getUpdater().getIdentificationName(), value, PacketIOUpdateCacheObject.Action.UPDATE)
            )
    }

    override fun getCloudPlayer(uniqueId: UUID): ICommunicationPromise<ICloudPlayer> {
        val cachedCloudPlayer = getCachedCloudPlayer(uniqueId)
        if (cachedCloudPlayer != null) {
            return CommunicationPromise.of(cachedCloudPlayer)
        }
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetCloudPlayer(uniqueId))
    }

    override fun getCloudPlayer(name: String): ICommunicationPromise<ICloudPlayer> {
        val cachedCloudPlayer = getCachedCloudPlayer(name)
        if (cachedCloudPlayer != null) {
            return CommunicationPromise.of(cachedCloudPlayer)
        }
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetCloudPlayer(name))
    }

    override fun setUpdates(cloudPlayer: ICloudPlayer, update: Boolean, serviceName: String) {
        super.setUpdates(cloudPlayer, update, serviceName)
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOSetCloudPlayerUpdates(cloudPlayer, update, serviceName))
    }

    override fun getOfflineCloudPlayer(name: String): ICommunicationPromise<IOfflineCloudPlayer> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetOfflinePlayer(name))
    }

    override fun getOfflineCloudPlayer(uniqueId: UUID): ICommunicationPromise<IOfflineCloudPlayer> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetOfflinePlayer(uniqueId))
    }

    override fun getAllOnlinePlayers(): ICommunicationPromise<List<SimpleCloudPlayer>> {
        return CloudPlugin.instance.communicationClient.sendQuery<Array<SimpleCloudPlayer>>(PacketIOGetAllOnlinePlayers())
                .then { it.toList() }
    }

}