package eu.thesimplecloud.api.wrapper.impl

import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.api.wrapper.IWrapperManager
import eu.thesimplecloud.api.wrapper.IWritableWrapperInfo
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

open class DefaultWrapperManager : IWrapperManager {

    private val wrappers = CopyOnWriteArrayList<IWrapperInfo>()

    override fun updateWrapper(wrapper: IWrapperInfo) {
        val cashedWrapper = getWrapperByHost(wrapper.getHost())
        if (cashedWrapper == null) {
            this.wrappers.add(wrapper)
            return
        }
        cashedWrapper as IWritableWrapperInfo
        cashedWrapper.setMaxMemory(wrapper.getMaxMemory())
        cashedWrapper.setMaxSimultaneouslyStartingServices(wrapper.getMaxSimultaneouslyStartingServices())
        cashedWrapper.setUsedMemory(wrapper.getUsedMemory())
        cashedWrapper.setAuthenticated(wrapper.isAuthenticated())
        cashedWrapper.setTemplatesReceived(wrapper.hasTemplatesReceived())
    }

    override fun removeWrapper(wrapper: IWrapperInfo) {
        this.wrappers.remove(wrapper)
    }

    override fun getAllWrappers(): Collection<IWrapperInfo> = this.wrappers
}