package eu.thesimplecloud.api.wrapper.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.wrapper.WrapperUpdatedEvent
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.api.wrapper.IWrapperManager
import eu.thesimplecloud.api.wrapper.IWritableWrapperInfo
import java.util.concurrent.CopyOnWriteArrayList

open class DefaultWrapperManager : IWrapperManager {

    private val wrappers = CopyOnWriteArrayList<IWrapperInfo>()

    override fun updateWrapper(wrapper: IWrapperInfo) {
        val cachedWrapper = getWrapperByHost(wrapper.getHost())
        if (cachedWrapper == null) {
            this.wrappers.add(wrapper)
            return
        }
        cachedWrapper as IWritableWrapperInfo
        cachedWrapper.setMaxMemory(wrapper.getMaxMemory())
        cachedWrapper.setMaxSimultaneouslyStartingServices(wrapper.getMaxSimultaneouslyStartingServices())
        cachedWrapper.setUsedMemory(wrapper.getUsedMemory())
        cachedWrapper.setAuthenticated(wrapper.isAuthenticated())
        cachedWrapper.setTemplatesReceived(wrapper.hasTemplatesReceived())
        cachedWrapper.setCurrentlyStartingServices(wrapper.getCurrentlyStartingServices())
        CloudAPI.instance.getEventManager().call(WrapperUpdatedEvent(cachedWrapper))
    }

    override fun removeWrapper(wrapper: IWrapperInfo) {
        this.wrappers.remove(wrapper)
    }

    override fun getAllWrappers(): Collection<IWrapperInfo> = this.wrappers
}