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

package eu.thesimplecloud.api.network.packets.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 20.11.2020
 * Time: 18:50
 * @author Frederick Baier
 */
class PacketIOSendTablistToPlayer() : JsonPacket() {

    constructor(uniqueId: UUID, headers: Array<String>, footers: Array<String>) : this() {
        this.jsonLib.append("uniqueId", uniqueId)
                .append("headers", headers)
                .append("footers", footers)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val uniqueId = this.jsonLib.getObject("uniqueId", UUID::class.java) ?: return contentException("uniqueId")
        val headers = this.jsonLib.getObject("headers", Array<String>::class.java) ?: return contentException("headers")
        val footers = this.jsonLib.getObject("footers", Array<String>::class.java) ?: return contentException("footers")
        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(uniqueId)
        cloudPlayer ?: return failure(NoSuchPlayerException("Player cannot be found"))
        cloudPlayer.sendTablist(headers, footers)
        return unit()
    }


}