package eu.thesimplecloud.api.cachelist

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.network.packets.sync.cachelist.PacketIOUpdateCacheObject
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.combineAllPromises

interface ICacheList<T : Any> {

    /**
     * Updates the cashed value with the content of the specified one.
     * @param value the value to update
     * @param fromPacket whether this method was called by a packet
     */
    fun update(value: T, fromPacket: Boolean = false, isCalledFromDelete: Boolean = false) {
        val updater = getUpdater()
        val cachedValue = updater.getCachedObjectByUpdateValue(value)
        val eventsToCall = updater.determineEventsToCall(value, cachedValue)
        if (cachedValue == null) {
            updater.addNewValue(value)
        } else {
            updater.mergeUpdateValue(value, cachedValue)
        }
        eventsToCall.forEach { CloudAPI.instance.getEventManager().call(it) }
        if (shallSpreadUpdates())
            if (!isCalledFromDelete)
                if (CloudAPI.instance.isManager() || !fromPacket)
                    updater.sendUpdatesToOtherComponents(value, PacketIOUpdateCacheObject.Action.UPDATE)
    }

    /**
     * Returns whether the updates shall be spread to other network components.
     */
    fun shallSpreadUpdates(): Boolean

    /**
     * Returns ht update lifecycle of this object
     */
    fun getUpdater(): ICacheObjectUpdater<T>

    /**
     * Deletes the specified value.
     * @param value the object to delete
     * @param fromPacket whether this method was called by a packet
     */
    fun delete(value: T, fromPacket: Boolean = false) {
        val updater = getUpdater()
        if (shallSpreadUpdates())
            if (CloudAPI.instance.isManager() || !fromPacket)
                updater.sendUpdatesToOtherComponents(value, PacketIOUpdateCacheObject.Action.DELETE)
    }

    /**
     * Returns all objects in this cache
     */
    fun getAllCachedObjects(): List<T>

    /**
     * Sends the update of the [value] to the specified [connection]
     * @param value the object to send
     * @param connection the connection to send the [value] to
     * @return the a promise that completes when the packet was handled.
     */
    fun sendUpdateToConnection(value: T, connection: IConnection): ICommunicationPromise<Unit> {
        return connection.sendUnitQuery(PacketIOUpdateCacheObject(getUpdater().getIdentificationName(), value, PacketIOUpdateCacheObject.Action.UPDATE))
    }

    /**
     * Sends the delete request of the [value] to the specified [connection]
     * @param value the object to send
     * @param connection the connection to send the [value] to
     * @return the a promise that completes when the packet was handled.
     */
    fun sendDeleteToConnection(value: T, connection: IConnection): ICommunicationPromise<Unit> {
        return connection.sendUnitQuery(PacketIOUpdateCacheObject(getUpdater().getIdentificationName(), value, PacketIOUpdateCacheObject.Action.DELETE))
    }

    /**
     * Sends all cached objects to the specified [connection]
     * @return a promise that completes when all packet have been sent.
     */
    fun sendAllCachedObjectsToConnection(connection: IConnection): ICommunicationPromise<Unit> {
        return getAllCachedObjects().map { this.sendUpdateToConnection(it, connection) }.combineAllPromises()
    }

}