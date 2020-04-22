package eu.thesimplecloud.api.sync.list

import eu.thesimplecloud.api.network.packets.sync.list.PacketIOUpdateSynchronizedListObject
import eu.thesimplecloud.api.sync.`object`.SynchronizedObjectHolder
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface ISynchronizedObjectList<T : ISynchronizedListObject> {

    /**
     * Returns the unique name to identify this [ISynchronizedObjectList]
     */
    fun getIdentificationName(): String

    /**
     * Returns all cached objects.
     */
    fun getAllCachedObjects(): Collection<SynchronizedObjectHolder<T>>

    /**
     * Updates an object
     */
    fun update(value: T, fromPacket: Boolean = false)

    /**
     * Returns the cashed value found by the [value] to update.
     */
    fun getCachedObjectByUpdateValue(value: T): SynchronizedObjectHolder<T>?

    /**
     * Removes the [value] object from the cache.
     */
    fun remove(value: T, fromPacket: Boolean = false)

    /**
     * Sends an update to one connection
     * @return the a promise that completes when the packet was handled.
     */
    fun updateToConnection(value: T, connection: IConnection): ICommunicationPromise<Unit> {
        return connection.sendUnitQuery(PacketIOUpdateSynchronizedListObject(getIdentificationName(), value))
    }

}