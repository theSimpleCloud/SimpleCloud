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

package eu.thesimplecloud.api.message

import eu.thesimplecloud.api.network.component.INetworkComponent
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.utils.Nameable
import eu.thesimplecloud.api.wrapper.IWrapperInfo

interface IMessageChannel<T> : Nameable {

    /**
     * Returns the class of this [IMessageChannel]
     */
    fun getMessageClass(): Class<T>

    /**
     * Registers a listener
     */
    fun registerListener(messageListener: IMessageListener<T>)

    /**
     * Unregisters a listener
     */
    fun unregisterListener(messageListener: IMessageListener<T>)

    /**
     * Sends a message
     * As receiver a [ICloudService] or [IWrapperInfo] can be used.
     * If you want to send a message to the manager you must use [INetworkComponent.MANAGER_COMPONENT]
     * @param msg the object to send
     * @param receivers the list of receivers
     */
    fun sendMessage(msg: T, receivers: List<INetworkComponent>)

    /**
     * Sends a message
     * As receiver a [ICloudService] or [IWrapperInfo] can be used.
     * If you want to send a message to the manager you must use [INetworkComponent.MANAGER_COMPONENT]
     * @param msg the object to send
     * @param receiver the receiver
     */
    fun sendMessage(msg: T, receiver: INetworkComponent) = sendMessage(msg, listOf(receiver))

}