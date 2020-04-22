package eu.thesimplecloud.api.wrapper

import eu.thesimplecloud.api.sync.`object`.SynchronizedObjectHolder
import eu.thesimplecloud.api.sync.list.ISynchronizedObjectList

interface IWrapperManager : ISynchronizedObjectList<IWrapperInfo> {

    /**
     * Returns the [IWrapperInfo] found by the specified name
     */
    fun getWrapperByName(name: String): SynchronizedObjectHolder<IWrapperInfo>? = getAllCachedObjects().firstOrNull { it.obj.getName().equals(name, true) }

    /**
     * Returns the [IWrapperInfo] found by the specified host
     */
    fun getWrapperByHost(host: String): SynchronizedObjectHolder<IWrapperInfo>? = getAllCachedObjects().firstOrNull { it.obj.getHost().equals(host, true) }

    /**
     * Returns the [IWrapperInfo] that has enough memory, is authenticated and has received all templates.
     */
    fun getWrapperByUnusedMemory(memory: Int): IWrapperInfo? {
        val wrappers = getAllCachedObjects().map { it.obj }
                .filter { it.isAuthenticated() && it.hasTemplatesReceived() && it.hasEnoughMemory(memory) }
                .filter { it.getCurrentlyStartingServices() != it.getMaxSimultaneouslyStartingServices() }
        return wrappers.minBy { it.getUsedMemory().toDouble() / it.getMaxMemory() }
    }



}