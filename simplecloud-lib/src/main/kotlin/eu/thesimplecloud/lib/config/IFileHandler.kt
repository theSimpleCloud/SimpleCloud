package eu.thesimplecloud.lib.config

interface IFileHandler<T> {

    /**
     * Saves the specified value to a file
     */
    fun save(value: T)

    /**
     * Deletes the file found yb the specified value
     */
    fun delete(value: T)

    /**
     * Loads all saved objects
     */
    fun loadAll(): Set<T>

}