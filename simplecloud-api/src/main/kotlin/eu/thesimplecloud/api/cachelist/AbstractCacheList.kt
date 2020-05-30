package eu.thesimplecloud.api.cachelist

import java.util.concurrent.CopyOnWriteArrayList

abstract class AbstractCacheList<T : Any>(private val spreadUpdates: Boolean = true) : ICacheList<T> {

    protected val values = CopyOnWriteArrayList<T>()

    override fun shallSpreadUpdates(): Boolean {
        return this.spreadUpdates
    }

    override fun delete(value: T, fromPacket: Boolean) {
        super.update(value, fromPacket = true, isCalledFromDelete = true)
        super.delete(value, fromPacket)
        this.values.remove(getUpdater().getCachedObjectByUpdateValue(value))
    }

    override fun getAllCachedObjects(): List<T> {
        return this.values
    }
}