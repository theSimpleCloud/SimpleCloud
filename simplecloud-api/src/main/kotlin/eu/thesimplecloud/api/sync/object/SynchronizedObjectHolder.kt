package eu.thesimplecloud.api.sync.`object`

/**
 * This is the holder of a synchronized object.
 * The holder will never change so it can be referenced
 */
data class SynchronizedObjectHolder<T : ISynchronizedObject>(var obj: T)