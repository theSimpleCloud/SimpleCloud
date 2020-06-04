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

package eu.thesimplecloud.base.wrapper.network.packets


import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.wrapper.IWritableWrapperInfo
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketInSetWrapperName : ObjectPacket<String>() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val name = this.value ?: return contentException("value")
        Wrapper.instance.thisWrapperName = name
        Wrapper.instance.startProcessQueue()
        if (Wrapper.instance.isStartedInManagerDirectory()) {
            val thisWrapper = Wrapper.instance.getThisWrapper()
            thisWrapper as IWritableWrapperInfo
            thisWrapper.setTemplatesReceived(true)
            CloudAPI.instance.getWrapperManager().update(thisWrapper)
        }
        return unit()
    }
}