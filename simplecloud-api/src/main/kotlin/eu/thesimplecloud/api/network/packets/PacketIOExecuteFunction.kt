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

package eu.thesimplecloud.api.network.packets

import eu.thesimplecloud.api.exception.SerializationException
import eu.thesimplecloud.api.utils.NoArgsFunction
import eu.thesimplecloud.api.utils.ObjectSerializer
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOExecuteFunction<T : Any>() : BytePacket() {


    constructor(function: () -> T) : this() {
        val noArgsFunction = object: NoArgsFunction<T> {
            override fun invoke(): T {
                return function()
            }
        }
        val serializeString = ObjectSerializer.serialize(noArgsFunction)
        this.buffer.writeBytes(serializeString.toByteArray(Charsets.ISO_8859_1))
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val string = String(this.buffer.array(), Charsets.ISO_8859_1)
        val noArgsFunction = ObjectSerializer.deserialize<NoArgsFunction<*>>(string) as NoArgsFunction<out Any>?
        noArgsFunction ?: return failure(SerializationException("Object was null"))
        return success(noArgsFunction.invoke())
    }
}