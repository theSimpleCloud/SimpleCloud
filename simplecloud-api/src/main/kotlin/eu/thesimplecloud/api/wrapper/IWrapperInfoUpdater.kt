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

package eu.thesimplecloud.api.wrapper

import eu.thesimplecloud.api.cachelist.value.ICacheValueUpdater
import eu.thesimplecloud.api.network.component.IAuthenticatable

interface IWrapperInfoUpdater : ICacheValueUpdater, IAuthenticatable {

    fun getWrapperInfo(): IWrapperInfo

    /**
     * Sets the amount of services this wrapper can start simultaneously
     */
    fun setMaxSimultaneouslyStartingServices(amount: Int)

    /**
     * Returns the amount of services this wrapper can start simultaneously
     */
    fun getMaxSimultaneouslyStartingServices(): Int

    /**
     * Sets the maximum MB of RAM for this wrapper
     */
    fun setMaxMemory(memory: Int)

    /**
     * Returns the maximum MB of RAM for this wrapper
     */
    fun getMaxMemory(): Int

    /**
     * Sets the amount of RAM in MB the wrapper currently uses
     */
    fun setUsedMemory(memory: Int)

    /**
     * Return the amount of RAM in MB the wrapper currently uses
     */
    fun getUsedMemory(): Int

    /**
     * Sets the CPU usage of this wrapper
     */
    fun setCpuUsage(usage: Float)

    /**
     * Returns the CPU usage of this wrapper
     */
    fun getCpuUsage(): Float

    /**
     * Sets whether the wrapper has received the templates
     */
    fun setTemplatesReceived(boolean: Boolean)

    /**
     * Returns whether the wrapper has received the templates
     */
    fun hasTemplatesReceived(): Boolean

    /**
     * Sets the amount of services this wrapper is currently starting
     */
    fun setCurrentlyStartingServices(startingServices: Int)

    /**
     * Return the amount of services this wrapper is currently starting
     */
    fun getCurrentlyStartingServices(): Int

}