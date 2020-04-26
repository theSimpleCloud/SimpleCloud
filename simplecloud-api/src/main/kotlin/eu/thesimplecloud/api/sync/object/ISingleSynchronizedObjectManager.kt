package eu.thesimplecloud.api.sync.`object`

import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface ISingleSynchronizedObjectManager {

    /**
     * Updates a synchronized object.
     * @return the [SynchronizedObjectHolder] of the specified [synchronizedObject]
     */
    fun <T : ISingleSynchronizedObject> updateObject(synchronizedObject: T, fromPacket: Boolean = false): SynchronizedObjectHolder<T>

    /**
     * Returns the [ISingleSynchronizedObject] found by the specified [name] or null if no object was found.
     */
    fun <T : ISingleSynchronizedObject> getObject(name: String): SynchronizedObjectHolder<T>?

    /**
     * Returns a [ICommunicationPromise] that completes once the requested [ISingleSynchronizedObject] is available.
     */
    fun <T : ISingleSynchronizedObject> requestSingleSynchronizedObject(name: String, clazz: Class<T>): ICommunicationPromise<SynchronizedObjectHolder<T>>

}