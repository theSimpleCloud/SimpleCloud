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

package eu.thesimplecloud.api.network.packets.sync.list

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.api.sync.list.ISynchronizedObjectList
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIORemoveListProperty() : JsonPacket() {

    constructor(listName: String, property: IProperty<*>) : this() {
        this.jsonLib.append("listName", listName).append("property", property)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val listName = this.jsonLib.getString("listName") ?: return contentException("listName")
        val property = this.jsonLib.getObject("property", Property::class.java) ?: return contentException("property")
        property as IProperty<Any>
        try {
            val synchronizedObjectList: ISynchronizedObjectList<Any>? = CloudAPI.instance.getSynchronizedObjectListManager().getSynchronizedObjectList(listName)
            synchronizedObjectList ?: return failure(NoSuchElementException())
            synchronizedObjectList.remove(property, true)

        } catch (ex: Exception) {
            throw ex
        }
        return unit()
    }
}