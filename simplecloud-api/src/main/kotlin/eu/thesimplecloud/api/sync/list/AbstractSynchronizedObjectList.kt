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

package eu.thesimplecloud.api.sync.list

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.sync.list.SynchronizedListObjectRemovedEvent
import eu.thesimplecloud.api.event.sync.list.SynchronizedListObjectUpdatedEvent
import eu.thesimplecloud.api.extension.sendPacketToAllAuthenticatedNonWrapperClients
import eu.thesimplecloud.api.network.packets.sync.list.PacketIORemoveSynchronizedListObject
import eu.thesimplecloud.api.network.packets.sync.list.PacketIOUpdateSynchronizedListObject
import eu.thesimplecloud.api.sync.`object`.SynchronizedObjectHolder
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.server.INettyServer
import java.util.concurrent.CopyOnWriteArrayList

abstract class AbstractSynchronizedObjectList<T : ISynchronizedListObject> : ISynchronizedObjectList<T> {

    protected val values = CopyOnWriteArrayList<SynchronizedObjectHolder<T>>()

    override fun update(value: T, fromPacket: Boolean) {
        val cachedValueHolder = getCachedObjectByUpdateValue(value)
        if (cachedValueHolder == null) {
            this.values.add(SynchronizedObjectHolder(value))
        } else {
            if (cachedValueHolder.obj !== value) {
                cachedValueHolder.obj = value
            }
        }
        if (CloudAPI.instance.isManager() || fromPacket) {
            CloudAPI.instance.getEventManager().call(SynchronizedListObjectUpdatedEvent(getCachedObjectByUpdateValue(value)!! as SynchronizedObjectHolder<ISynchronizedListObject>))
        }
        forwardPacketIfNecessary(PacketIOUpdateSynchronizedListObject(getIdentificationName(), value), fromPacket)
    }

    override fun getAllCachedObjects(): Collection<SynchronizedObjectHolder<T>> = this.values

    override fun remove(value: T, fromPacket: Boolean) {
        val cachedObject = getCachedObjectByUpdateValue(value) ?: return


        if (CloudAPI.instance.isManager() || fromPacket) {
            this.values.remove(cachedObject)
            CloudAPI.instance.getEventManager().call(SynchronizedListObjectRemovedEvent(cachedObject as SynchronizedObjectHolder<ISynchronizedListObject>))
        }

        forwardPacketIfNecessary(PacketIORemoveSynchronizedListObject(getIdentificationName(), value), fromPacket)
    }

    private fun forwardPacketIfNecessary(packet: IPacket, fromPacket: Boolean) {
        if (CloudAPI.instance.isManager()) {
            val server = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyServer<*>
            server.getClientManager().sendPacketToAllAuthenticatedNonWrapperClients(packet)
        } else if (!fromPacket) {
            //send update to the manager
            val client = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyClient
            client.sendUnitQuery(packet)
        }
    }

}