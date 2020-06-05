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

package eu.thesimplecloud.api.wrapper.impl

import eu.thesimplecloud.api.cachelist.AbstractCacheList
import eu.thesimplecloud.api.cachelist.ICacheObjectUpdater
import eu.thesimplecloud.api.event.wrapper.WrapperUpdatedEvent
import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.api.wrapper.IWrapperManager
import eu.thesimplecloud.api.wrapper.IWritableWrapperInfo

open class DefaultWrapperManager : AbstractCacheList<IWrapperInfo>(), IWrapperManager {

    private val updateLifecycle = object : ICacheObjectUpdater<IWrapperInfo> {

        override fun getCachedObjectByUpdateValue(value: IWrapperInfo): IWrapperInfo? {
            return getWrapperByName(value.getName())
        }

        override fun determineEventsToCall(updateValue: IWrapperInfo, cachedValue: IWrapperInfo?): List<IEvent> {
            val wrapperToUse = cachedValue ?: updateValue
            return listOf(WrapperUpdatedEvent(wrapperToUse))
        }

        override fun mergeUpdateValue(updateValue: IWrapperInfo, cachedValue: IWrapperInfo) {
            cachedValue as IWritableWrapperInfo

            cachedValue.setMaxMemory(updateValue.getMaxMemory())
            cachedValue.setUsedMemory(updateValue.getUsedMemory())

            cachedValue.setCurrentlyStartingServices(updateValue.getCurrentlyStartingServices())
            cachedValue.setMaxSimultaneouslyStartingServices(updateValue.getMaxSimultaneouslyStartingServices())

            cachedValue.setTemplatesReceived(updateValue.hasTemplatesReceived())
            cachedValue.setAuthenticated(updateValue.isAuthenticated())
        }

        override fun addNewValue(value: IWrapperInfo) {
            values.add(value)
        }

        override fun getIdentificationName(): String {
            return "wrapper-cache"
        }

    }

    override fun getUpdater(): ICacheObjectUpdater<IWrapperInfo> {
        return this.updateLifecycle
    }


}