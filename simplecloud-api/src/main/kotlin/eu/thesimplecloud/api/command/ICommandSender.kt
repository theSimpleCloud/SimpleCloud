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

package eu.thesimplecloud.api.command

import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.api.exception.UnreachableComponentException
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface ICommandSender {

    /**
     * Sends a message to this [ICommandSender]
     * @return a promise that completes when the message was sent.
     */
    fun sendMessage(message: String): ICommunicationPromise<Unit>

    /**
     * Checks whether this sender has the specified [permission]
     * @return a promise that is completed when the permission is checked, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure. If the sender is a ConsoleSender this method returns true.
     * The promise will fail with:
     * - [UnreachableComponentException] if the proxy server the sender is connected is not reachable.
     * - [NoSuchPlayerException] if the sender cannot be found on the proxy.
     */
    fun hasPermission(permission: String): ICommunicationPromise<Boolean>

    /**
     * Checks if the sender has the specified [permission]
     * This method blocks until the result is ready
     * @return whether the player has the permission or false if there was an error
     */
    fun hasPermissionSync(permission: String): Boolean = hasPermission(permission).getBlockingOrNull() ?: false

}