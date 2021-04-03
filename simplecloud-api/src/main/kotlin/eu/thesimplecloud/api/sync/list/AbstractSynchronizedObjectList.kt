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
import eu.thesimplecloud.api.network.packets.sync.list.PacketIORemoveListProperty
import eu.thesimplecloud.api.network.packets.sync.list.PacketIOUpdateListProperty
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.server.INettyServer
import java.util.concurrent.CopyOnWriteArrayList

abstract class AbstractSynchronizedObjectList<T : Any>(
        protected val values: CopyOnWriteArrayList<Property<T>> = CopyOnWriteArrayList()
) : ISynchronizedObjectList<T> {


    override fun update(property: IProperty<T>, fromPacket: Boolean): ICommunicationPromise<Unit> {
        val cachedValue = getCachedObjectByUpdateValue(property.getValue())
        if (cachedValue == null) {
            this.values.add(property as Property<T>)
        } else {
            if (cachedValue !== property.getValue()) {
                cachedValue as Property<T>
                cachedValue.resetValue()
                cachedValue.setStringValue(property.getValueAsString())
            }
        }
        if (CloudAPI.instance.isManager() || fromPacket) {
            CloudAPI.instance.getEventManager().call(SynchronizedListObjectUpdatedEvent(getCachedObjectByUpdateValue(property.getValue())!!))
        }
        return forwardPacketIfNecessary(PacketIOUpdateListProperty(getIdentificationName(), property), fromPacket)
    }

    override fun getAllCachedObjects(): Collection<IProperty<T>> = this.values

    override fun remove(property: IProperty<T>, fromPacket: Boolean): ICommunicationPromise<Unit> {
        val cachedObject = getCachedObjectByUpdateValue(property.getValue()) ?: return CommunicationPromise.UNIT_PROMISE


        if (CloudAPI.instance.isManager() || fromPacket) {
            this.values.remove(cachedObject)
            CloudAPI.instance.getEventManager().call(SynchronizedListObjectRemovedEvent(cachedObject))
        }

        return forwardPacketIfNecessary(PacketIORemoveListProperty(getIdentificationName(), property), fromPacket)
    }

    private fun forwardPacketIfNecessary(packet: IPacket, fromPacket: Boolean): ICommunicationPromise<Unit> {
        if (CloudAPI.instance.isManager()) {
            val server = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyServer<*>
            return server.getClientManager().sendPacketToAllAuthenticatedNonWrapperClients(packet)
        } else if (!fromPacket) {
            //send update to the manager
            val client = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyClient
            return client.getConnection().sendUnitQuery(packet)
        }
        return CommunicationPromise.UNIT_PROMISE
    }

}