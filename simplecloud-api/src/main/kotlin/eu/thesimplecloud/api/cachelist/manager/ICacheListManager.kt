package eu.thesimplecloud.api.cachelist.manager

import eu.thesimplecloud.api.cachelist.ICacheList

interface ICacheListManager {

    /**
     * Registers a cache listener.
     */
    fun registerCacheList(cacheList: ICacheList<out Any>)

    /**
     * Returns the [ICacheList] found by the specified [name]
     */
    fun getCacheListenerByName(name: String): ICacheList<Any>?

}