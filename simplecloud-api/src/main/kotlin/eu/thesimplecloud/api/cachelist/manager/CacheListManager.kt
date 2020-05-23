package eu.thesimplecloud.api.cachelist.manager

import eu.thesimplecloud.api.cachelist.ICacheList
import java.util.concurrent.CopyOnWriteArrayList

class CacheListManager : ICacheListManager {

    private val cacheListList = CopyOnWriteArrayList<ICacheList<Any>>()

    override fun registerCacheList(cacheList: ICacheList<out Any>) {
        this.cacheListList.add(cacheList as ICacheList<Any>)
    }

    override fun getCacheListenerByName(name: String): ICacheList<Any>? {
        return this.cacheListList.firstOrNull { it.getUpdater().getIdentificationName() == name }
    }
}