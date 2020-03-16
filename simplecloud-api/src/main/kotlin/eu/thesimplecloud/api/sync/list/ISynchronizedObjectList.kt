package eu.thesimplecloud.api.sync.list

interface ISynchronizedObjectList<T : ISynchronizedListObject> {

    /**
     * Returns the unique name to identify this [ISynchronizedObjectList]
     */
    fun getIdentificationName(): String

    /**
     * Returns all cached objects.
     */
    fun getAllCachedObjects(): Collection<T>

    /**
     * Updates an object
     */
    fun update(value: T, fromPacket: Boolean = false)

    /**
     * Returns the cashed value found by the [value] to update.
     */
    fun getCachedObjectByUpdateValue(value: T): T?

    /**
     * Removes the [value] object from the cache.
     */
    fun remove(value: T, fromPacket: Boolean = false)

}