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

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.network.component.INetworkComponent
import eu.thesimplecloud.jsonlib.GsonCreator
import eu.thesimplecloud.jsonlib.JsonLib
import java.util.concurrent.CopyOnWriteArraySet


class MessageChannel<T>(private val name: String, private val clazz: Class<T>) : IMessageChannel<T> {

    companion object {
        private val GSON = GsonCreator().create()
    }

    private val listeners = CopyOnWriteArraySet<IMessageListener<T>>()

    override fun registerListener(messageListener: IMessageListener<T>) {
        this.listeners.add(messageListener)
    }

    override fun unregisterListener(messageListener: IMessageListener<T>) {
        this.listeners.remove(messageListener)
    }

    /**
     * Notifies all listeners.
     */
    fun notifyListeners(message: Message) {
        if (message.className != clazz.name)
            throw IllegalArgumentException("Invalid message class on message channel ${message.channel}: Expected ${clazz.name} but was ${message.className} ")
        val jsonLib = JsonLib.fromJsonString(message.messageString)
        val msg = jsonLib.getObject(clazz)
        val networkComponent = message.senderReference.getNetworkComponent()
                ?: throw IllegalArgumentException("Connected process of ${message.senderReference.name} is null")
        this.listeners.forEach { it.messageReceived(msg, networkComponent) }
    }

    override fun getName(): String {
        return this.name
    }

    override fun getMessageClass(): Class<T> {
        return this.clazz
    }

    override fun sendMessage(msg: T, receivers: List<INetworkComponent>) {
        val thisComponent = CloudAPI.instance.getThisSidesNetworkComponent()
        val messageString = GSON.toJson(msg)
        val message = Message(getName(), clazz.name, messageString, thisComponent.toNetworkComponentReference(), receivers.map { it.toNetworkComponentReference() })
        return (CloudAPI.instance.getMessageChannelManager() as MessageChannelManager)
                .sendMessage(message)
    }

}