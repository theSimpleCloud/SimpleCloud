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

package eu.thesimplecloud.api.network.packets.servicegroup

import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultLobbyGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultProxyGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultServerGroup
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

abstract class PacketIOCloudServiceGroupData() : JsonPacket() {

    constructor(cloudServiceGroup: ICloudServiceGroup): this() {
        this.jsonLib.append("serviceType", cloudServiceGroup.getServiceType()).append("group", cloudServiceGroup)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val serviceType = this.jsonLib.getObject("serviceType", ServiceType::class.java) ?: return contentException("serviceType")
        val serviceGroupClass = when(serviceType){
            ServiceType.LOBBY -> DefaultLobbyGroup::class.java
            ServiceType.SERVER -> DefaultServerGroup::class.java
            ServiceType.PROXY -> DefaultProxyGroup::class.java
        }
        val serviceGroup = this.jsonLib.getObject("group", serviceGroupClass) ?: return contentException("group")
        return handleData(serviceGroup)
    }

    abstract fun handleData(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<out Any>

}