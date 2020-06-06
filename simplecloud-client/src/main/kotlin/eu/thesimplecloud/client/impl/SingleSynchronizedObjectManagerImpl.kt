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

package eu.thesimplecloud.client.impl

import eu.thesimplecloud.api.network.packets.sync.`object`.PacketIOUpdateSingleSynchronizedObject
import eu.thesimplecloud.api.sync.`object`.AbstractSingleSynchronizedObjectManager
import eu.thesimplecloud.api.sync.`object`.ISingleSynchronizedObject
import eu.thesimplecloud.api.sync.`object`.SynchronizedObjectHolder
import eu.thesimplecloud.client.packets.PacketOutGetSynchronizedObject
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class SingleSynchronizedObjectManagerImpl(private val client: INettyClient) : AbstractSingleSynchronizedObjectManager() {


    override fun <T : ISingleSynchronizedObject> updateObject(synchronizedObject: T, fromPacket: Boolean): SynchronizedObjectHolder<T> {
        if (fromPacket) {
            return super.updateObject(synchronizedObject, fromPacket)
        } else {
            client.sendUnitQuery(PacketIOUpdateSingleSynchronizedObject(synchronizedObject))

            val cachedHolder = getObject<T>(synchronizedObject.getName())
            if (cachedHolder == null) {
                val newHolder: SynchronizedObjectHolder<T> = SynchronizedObjectHolder(synchronizedObject)
                this.nameToValue[synchronizedObject.getName()] = newHolder as SynchronizedObjectHolder<ISingleSynchronizedObject>
                return newHolder
            }
            return cachedHolder
        }
    }

    override fun <T : ISingleSynchronizedObject> requestSingleSynchronizedObject(name: String, clazz: Class<T>): ICommunicationPromise<SynchronizedObjectHolder<T>> {
        val objectPromise = client.sendQueryAsync(PacketOutGetSynchronizedObject(name), clazz, 2000)
        return objectPromise.then { updateObject(it, true) }
    }
}