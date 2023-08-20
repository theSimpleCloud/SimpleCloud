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

package eu.thesimplecloud.api.cachelist

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.cachelist.value.ICacheValue
import eu.thesimplecloud.api.cachelist.value.ICacheValueUpdater
import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.extension.sendPacketToAllAuthenticatedClients
import eu.thesimplecloud.api.network.packets.sync.cachelist.PacketIOUpdateCacheObject
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.server.INettyServer

interface ICacheObjectUpdateExecutor<U : ICacheValueUpdater, T : ICacheValue<U>> {

    /**
     * Returns the identification name
     */
    fun getIdentificationName(): String

    /**
     * Returns the cached object by the update value
     * @param value the update value
     * @return the value currently cached
     */
    fun getCachedObjectByUpdateValue(value: T): T?

    /**
     * This method will be invoked
     * the events will be called after the update value was updated to the cache
     * All events should be called with the [cachedValue] if it is not null
     */
    fun determineEventsToCall(updater: U, cachedValue: T?): List<IEvent>

    /**
     * Sends the [value] to every network component that shall receive the update.
     * This method is only called if [ICacheList.update] was invoked with fromPacket = false or
     * this side is the Manager
     */
    fun sendUpdatesToOtherComponents(value: T, action: PacketIOUpdateCacheObject.Action): ICommunicationPromise<Unit> {
        val packet = PacketIOUpdateCacheObject(getIdentificationName(), value, action)
        return if (CloudAPI.instance.isManager()) {
            val server = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyServer<*>
            server.getClientManager().sendPacketToAllAuthenticatedClients(packet)
        } else {
            val client = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyClient
            client.getConnection().sendUnitQuery(packet)
        }
    }

    /**
     * Adds the specified [value] to the cache
     */
    fun addNewValue(value: T)

}