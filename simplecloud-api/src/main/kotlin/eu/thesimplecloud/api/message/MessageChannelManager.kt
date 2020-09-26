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

package eu.thesimplecloud.api.message

import com.google.common.collect.Maps
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.client.NetworkComponentReference
import eu.thesimplecloud.api.client.NetworkComponentType
import eu.thesimplecloud.api.network.packets.message.PacketIOChannelMessage
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.server.INettyServer

class MessageChannelManager : IMessageChannelManager {

    private val channels = Maps.newConcurrentMap<String, MessageChannel<*>>()

    override fun <T> registerMessageChannel(name: String, clazz: Class<T>): IMessageChannel<T> {
        if (this.channels.containsKey(name)) throw IllegalArgumentException("Channel is already registered")
        val messageChannel = MessageChannel(name, clazz)
        this.channels[name] = messageChannel
        return messageChannel
    }

    override fun <T> getMessageChannelByName(name: String): IMessageChannel<T>? {
        return this.channels[name] as IMessageChannel<T>
    }

    fun sendMessage(message: Message) {
        val packetToSend = PacketIOChannelMessage(message)
        if (CloudAPI.instance.isManager()) {
            val server = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyServer<*>
            val allNotManagerReceivers = message.receivers
                    .filter { it.componentType != NetworkComponentType.MANAGER }
                    .mapNotNull { it.getNetworkComponent() }
            allNotManagerReceivers.forEach {
                val client = server.getClientManager().getClientByClientValue(it)
                client?.sendUnitQuery(packetToSend)
            }
        } else {
            val client = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyClient
            client.getConnection().sendUnitQuery(packetToSend)
        }
    }

    fun incomingMessage(message: Message) {
        val channel = this.channels[message.channel]
        if (CloudAPI.instance.isManager()){
            sendMessage(message)
            if (message.receivers.contains(NetworkComponentReference.MANAGER_COMPONENT_REFERENCE))
                channel?.notifyListeners(message)
        } else {
            channel?.notifyListeners(message)
        }
    }

}