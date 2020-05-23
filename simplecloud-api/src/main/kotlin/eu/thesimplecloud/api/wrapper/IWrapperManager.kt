package eu.thesimplecloud.api.wrapper

import eu.thesimplecloud.api.cachelist.ICacheList


interface IWrapperManager : ICacheList<IWrapperInfo> {

    /**
     * Returns the [IWrapperInfo] found by the specified name
     */
    fun getWrapperByName(name: String): IWrapperInfo? = getAllCachedObjects().firstOrNull { it.getName().equals(name, true) }

    /**
     * Returns the [IWrapperInfo] found by the specified host
     */
    fun getWrapperByHost(host: String): IWrapperInfo? = getAllCachedObjects().firstOrNull { it.getHost().equals(host, true) }

    /**
     * Returns the [IWrapperInfo] that has enough memory, is authenticated and has received all templates.
     */
    fun getWrapperByUnusedMemory(memory: Int): IWrapperInfo? {
        val wrappers = getAllCachedObjects()
                .filter { it.isAuthenticated() && it.hasTemplatesReceived() && it.hasEnoughMemory(memory) }
                .filter { it.getCurrentlyStartingServices() != it.getMaxSimultaneouslyStartingServices() }
        return wrappers.minBy { it.getUsedMemory().toDouble() / it.getMaxMemory() }
    }



}