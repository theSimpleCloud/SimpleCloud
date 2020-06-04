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

package eu.thesimplecloud.api.sync.list

import eu.thesimplecloud.api.network.packets.sync.list.PacketIOUpdateSynchronizedListObject
import eu.thesimplecloud.api.sync.`object`.SynchronizedObjectHolder
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface ISynchronizedObjectList<T : ISynchronizedListObject> {

    /**
     * Returns the unique name to identify this [ISynchronizedObjectList]
     */
    fun getIdentificationName(): String

    /**
     * Returns all cached objects.
     */
    fun getAllCachedObjects(): Collection<SynchronizedObjectHolder<T>>

    /**
     * Updates an object
     */
    fun update(value: T, fromPacket: Boolean = false)

    /**
     * Returns the cashed value found by the [value] to update.
     */
    fun getCachedObjectByUpdateValue(value: T): SynchronizedObjectHolder<T>?

    /**
     * Removes the [value] object from the cache.
     */
    fun remove(value: T, fromPacket: Boolean = false)

    /**
     * Sends an update to one connection
     * @return the a promise that completes when the packet was handled.
     */
    fun updateToConnection(value: T, connection: IConnection): ICommunicationPromise<Unit> {
        return connection.sendUnitQuery(PacketIOUpdateSynchronizedListObject(getIdentificationName(), value))
    }

}