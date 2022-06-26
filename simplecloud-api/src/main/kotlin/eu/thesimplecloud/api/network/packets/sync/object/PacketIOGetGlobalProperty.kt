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

package eu.thesimplecloud.api.network.packets.sync.`object`

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.sync.`object`.GlobalPropertyHolder
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * Created by IntelliJ IDEA.
 * Date: 21.06.2020
 * Time: 09:41
 * @author Frederick Baier
 */
class PacketIOGetGlobalProperty() : ObjectPacket<String>() {

    constructor(name: String) : this() {
        this.value = name
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        val value = this.value ?: return contentException("value")
        val globalPropertyHolder = CloudAPI.instance.getGlobalPropertyHolder()
                as GlobalPropertyHolder
        val property = globalPropertyHolder.getProperty<Any>(value)
        property?.let {
            globalPropertyHolder.addConnectionToUpdates(value, connection)
        }
        return CommunicationPromise.ofNullable(property, NoSuchElementException())
    }
}