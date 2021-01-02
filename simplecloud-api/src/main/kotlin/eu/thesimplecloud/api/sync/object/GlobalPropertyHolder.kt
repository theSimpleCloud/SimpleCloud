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

package eu.thesimplecloud.api.sync.`object`

import com.google.common.collect.Maps
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.sync.`object`.GlobalPropertyUpdatedEvent
import eu.thesimplecloud.api.network.packets.sync.`object`.PacketIOGetGlobalProperty
import eu.thesimplecloud.api.network.packets.sync.`object`.PacketIORemoveGlobalProperty
import eu.thesimplecloud.api.network.packets.sync.`object`.PacketIOUpdateGlobalProperty
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.concurrent.CopyOnWriteArraySet

class GlobalPropertyHolder : IGlobalPropertyHolder {

    private val propertyNameToUpdateClient = Maps.newConcurrentMap<String, MutableSet<IConnection>>()

    private val nameToValue: MutableMap<String, IProperty<*>> = Maps.newConcurrentMap()

    fun addConnectionToUpdates(propertyName: String, connection: IConnection) {
        val list = this.propertyNameToUpdateClient.getOrPut(propertyName) { CopyOnWriteArraySet() }
        list.add(connection)
    }

    fun removeConnectionFromUpdates(connection: IConnection) {
        this.propertyNameToUpdateClient.values.forEach { it.remove(connection) }
    }

    override fun getProperties(): Map<String, IProperty<*>> {
        return this.nameToValue
    }

    override fun <T : Any> setProperty(name: String, value: T): IProperty<T> {
        val newProperty = Property(value)
        writeUpdateToMap(name, newProperty)
        updatePropertyToNetwork(name, newProperty)
        return newProperty
    }

    override fun clearProperties() {
        throw UnsupportedOperationException("Cannot clear global properties")
    }

    override fun removeProperty(name: String) {
        this.nameToValue.remove(name)
        removePropertyFromNetwork(name)
    }

    override fun <T : Any> requestProperty(name: String): ICommunicationPromise<IProperty<T>> {
        if (CloudAPI.instance.isManager()) throw UnsupportedOperationException("Cannot request properties from manager")
        val client = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyClient
        return client.getConnection().sendQuery<IProperty<T>>(PacketIOGetGlobalProperty(name), 5000)
                .addResultListener { updatePropertyFromPacket(name, it) }
    }

    fun updatePropertyFromPacket(name: String, property: IProperty<*>) {
        writeUpdateToMap(name, property)
        CloudAPI.instance.getEventManager().call(GlobalPropertyUpdatedEvent(name, property))

        if (CloudAPI.instance.isManager())
            updatePropertyToNetwork(name, property)
    }

    fun removePropertyFromPacket(name: String) {
        this.nameToValue.remove(name)

        if (CloudAPI.instance.isManager())
            removePropertyFromNetwork(name)
    }

    private fun writeUpdateToMap(name: String, property: IProperty<*>) {
        val cachedProperty = this.nameToValue[name]
        if (cachedProperty == null) {
            this.nameToValue[name] = property
            return
        }
        cachedProperty as Property<*>
        cachedProperty.setStringValue(property.getValueAsString())
        cachedProperty.resetValue()
    }

    private fun updatePropertyToNetwork(name: String, property: IProperty<*>) {
        val updatePacket = PacketIOUpdateGlobalProperty(name, property)
        forwardPacket(name, updatePacket)
    }

    private fun removePropertyFromNetwork(name: String) {
        val updatePacket = PacketIORemoveGlobalProperty(name)
        forwardPacket(name, updatePacket)
    }

    private fun forwardPacket(name: String, packet: IPacket) {
        if (CloudAPI.instance.isManager()) {
            this.propertyNameToUpdateClient[name]?.forEach {
                it.sendUnitQuery(packet)
            }
        } else {
            val client = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyClient
            client.getConnection().sendUnitQuery(packet)
        }
    }

}