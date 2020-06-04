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

package eu.thesimplecloud.api.wrapper


import eu.thesimplecloud.api.client.NetworkComponentType
import eu.thesimplecloud.api.utils.INetworkComponent


interface IWrapperInfo : INetworkComponent {

    /**
     * Returns the host of this wrapper.
     */
    fun getHost(): String

    /**
     * Returns the amount of services this wrapper can start simultaneously
     */
    fun getMaxSimultaneouslyStartingServices(): Int

    /**
     * Returns the amount of RAM the wrapper uses at the moment in MB
     */
    fun getUsedMemory(): Int

    /**
     * Returns the amount of RAM this wrapper has at maximum
     */
    fun getMaxMemory(): Int

    /**
     * Returns the amount of RAM the wrapper has left
     */
    fun getUnusedMemory(): Int {
        return getMaxMemory() - getUsedMemory()
    }

    /**
     * Returns whether this wrapper has the specified memory left
     */
    fun hasEnoughMemory(memory: Int): Boolean {
        return getUnusedMemory() >= memory
    }

    /**
     * Returns whether the wrapper has received the templates.
     */
    fun hasTemplatesReceived(): Boolean

    /**
     * Returns the amount of services this wrapper is currently starting
     */
    fun getCurrentlyStartingServices(): Int

    override fun getNetworkComponentType(): NetworkComponentType = NetworkComponentType.WRAPPER

}