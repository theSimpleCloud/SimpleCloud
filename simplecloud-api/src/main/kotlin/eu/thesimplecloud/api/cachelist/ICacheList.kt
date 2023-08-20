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
import eu.thesimplecloud.api.network.packets.sync.cachelist.PacketIOUpdateCacheObject
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.combineAllPromises

interface ICacheList<U : ICacheValueUpdater, T : ICacheValue<U>> {

    /**
     * Updates the cashed value with the content of the specified one.
     * @param value the value to update
     * @param fromPacket whether this method was called by a packet
     * @return a promise that completes when the update was sent
     */
    fun update(
        value: T,
        fromPacket: Boolean = false,
        isCalledFromDelete: Boolean = false
    ): ICommunicationPromise<Unit> {
        val valueUpdater = value.getUpdater()
        val updateExecutor = getUpdateExecutor()
        val cachedValue = updateExecutor.getCachedObjectByUpdateValue(value)
        val eventsToCall = updateExecutor.determineEventsToCall(valueUpdater, cachedValue)
        if (cachedValue == null) {
            updateExecutor.addNewValue(value)
        } else {
            cachedValue.applyValuesFromUpdater(valueUpdater)
        }
        eventsToCall.forEach { CloudAPI.instance.getEventManager().call(it) }

        if (shallSpreadUpdates())
            if (!isCalledFromDelete)
                if (CloudAPI.instance.isManager() || !fromPacket)
                    return updateExecutor.sendUpdatesToOtherComponents(value, PacketIOUpdateCacheObject.Action.UPDATE)

        return CommunicationPromise.UNIT_PROMISE
    }

    /**
     * Returns whether the updates shall be spread to other network components.
     */
    fun shallSpreadUpdates(): Boolean

    /**
     * Returns ht update lifecycle of this object
     */
    fun getUpdateExecutor(): ICacheObjectUpdateExecutor<U, T>

    /**
     * Deletes the specified value.
     * @param value the object to delete
     * @param fromPacket whether this method was called by a packet
     * @return a promise that completes when the delete was sent
     */
    fun delete(value: T, fromPacket: Boolean = false): ICommunicationPromise<Unit> {
        val updater = getUpdateExecutor()
        if (shallSpreadUpdates())
            if (CloudAPI.instance.isManager() || !fromPacket)
                return updater.sendUpdatesToOtherComponents(value, PacketIOUpdateCacheObject.Action.DELETE)

        return CommunicationPromise.UNIT_PROMISE
    }

    /**
     * Returns all objects in this cache
     */
    fun getAllCachedObjects(): List<T>

    /**
     * Sends the update of the [value] to the specified [connection]
     * @param value the object to send
     * @param connection the connection to send the [value] to
     * @return the a promise that completes when the packet was handled.
     */
    fun sendUpdateToConnection(value: T, connection: IConnection): ICommunicationPromise<Unit> {
        return connection.sendUnitQuery(
            PacketIOUpdateCacheObject(
                getUpdateExecutor().getIdentificationName(),
                value,
                PacketIOUpdateCacheObject.Action.UPDATE
            )
        )
    }

    /**
     * Sends the delete request of the [value] to the specified [connection]
     * @param value the object to send
     * @param connection the connection to send the [value] to
     * @return the a promise that completes when the packet was handled.
     */
    fun sendDeleteToConnection(value: T, connection: IConnection): ICommunicationPromise<Unit> {
        return connection.sendUnitQuery(
            PacketIOUpdateCacheObject(
                getUpdateExecutor().getIdentificationName(),
                value,
                PacketIOUpdateCacheObject.Action.DELETE
            )
        )
    }

    /**
     * Sends all cached objects to the specified [connection]
     * @return a promise that completes when all packet have been sent.
     */
    fun sendAllCachedObjectsToConnection(connection: IConnection): ICommunicationPromise<Unit> {
        return getAllCachedObjects().map { this.sendUpdateToConnection(it, connection) }.combineAllPromises()
    }

}