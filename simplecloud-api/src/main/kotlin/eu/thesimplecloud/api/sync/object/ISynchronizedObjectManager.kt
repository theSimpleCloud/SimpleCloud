package eu.thesimplecloud.api.sync.`object`

import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface ISynchronizedObjectManager {

    /**
     * Updates a synchronized object.
     */
    fun updateObject(synchronizedObject: ISynchronizedObject, fromPacket: Boolean = false)

    /**
     * Returns the [ISynchronizedObject] found by the specified [name] or null if no object was found.
     */
    fun <T : ISynchronizedObject> getObject(name: String): T?

    /**
     * Returns a [ICommunicationPromise] that completes once the requested [ISynchronizedObject] is available.
     */
    fun <T : ISynchronizedObject> requestSynchronizedObject(name: String, clazz: Class<T>): ICommunicationPromise<T>

}