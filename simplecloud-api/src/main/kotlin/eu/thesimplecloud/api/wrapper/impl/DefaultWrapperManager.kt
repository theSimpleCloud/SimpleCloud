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
            cachedValue.setUsedMemory(updateValue.getUnusedMemory())

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