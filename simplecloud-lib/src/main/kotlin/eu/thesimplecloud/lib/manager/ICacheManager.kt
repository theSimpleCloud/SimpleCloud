package eu.thesimplecloud.lib.manager

interface ICacheManager<T> {

    /**
     * Updates the cashed value with the content of the specified one.
     */
    fun updateCache(value: T)

    /**
     * Deletes the specified value.
     */
    fun deleteFromCache(value: T)

    /**
     * Clears the cache
     */
    fun clearCache()

}