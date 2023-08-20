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

package eu.thesimplecloud.api.wrapper.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.cachelist.value.AbstractCacheValueUpdater
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.api.wrapper.IWrapperInfoUpdater
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class DefaultWrapperInfoUpdater(
    private val wrapperInfo: IWrapperInfo
) : AbstractCacheValueUpdater(), IWrapperInfoUpdater {

    override fun getWrapperInfo(): IWrapperInfo {
        return wrapperInfo
    }

    override fun setMaxSimultaneouslyStartingServices(amount: Int) {
        changes["simultaneouslyStartingServices"] = amount
    }

    override fun getMaxSimultaneouslyStartingServices(): Int {
        return getChangedValue("simultaneouslyStartingServices") ?: wrapperInfo.getMaxSimultaneouslyStartingServices()
    }

    override fun setMaxMemory(memory: Int) {
        changes["maxMemory"] = memory
    }

    override fun getMaxMemory(): Int {
        return getChangedValue("maxMemory") ?: wrapperInfo.getMaxMemory()
    }

    override fun setUsedMemory(memory: Int) {
        changes["usedMemory"] = memory
    }

    override fun getUsedMemory(): Int {
        return getChangedValue("usedMemory") ?: wrapperInfo.getUsedMemory()
    }

    override fun setCpuUsage(usage: Float) {
        changes["cpuUsage"] = usage
    }

    override fun getCpuUsage(): Float {
        return getChangedValue("cpuUsage") ?: wrapperInfo.getCpuUsage()
    }

    override fun setTemplatesReceived(boolean: Boolean) {
        changes["templatesReceived"] = boolean
    }

    override fun hasTemplatesReceived(): Boolean {
        return getChangedValue("templatesReceived") ?: wrapperInfo.hasTemplatesReceived()
    }

    override fun setCurrentlyStartingServices(startingServices: Int) {
        changes["currentlyStartingServices"] = startingServices
    }

    override fun getCurrentlyStartingServices(): Int {
        return getChangedValue("currentlyStartingServices") ?: wrapperInfo.getCurrentlyStartingServices()
    }

    override fun setAuthenticated(authenticated: Boolean) {
        changes["authenticated"] = authenticated
    }

    override fun isAuthenticated(): Boolean {
        return getChangedValue("authenticated") ?: wrapperInfo.isAuthenticated()
    }

    override fun update(): ICommunicationPromise<Unit> {
        return CloudAPI.instance.getWrapperManager().update(wrapperInfo)
    }

}