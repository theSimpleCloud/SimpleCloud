package eu.thesimplecloud.api.cachelist

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.extension.sendPacketToAllAuthenticatedClients
import eu.thesimplecloud.api.network.packets.sync.cachelist.PacketIOUpdateCacheObject
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.server.INettyServer

interface ICacheObjectUpdater<T : Any> {

    /**
     * Returns the identification name
     */
    fun getIdentificationName(): String

    /**
     * Returns the cached object by the update value
     * @param value the update value
     * @return the value currently cached
     */
    fun getCachedObjectByUpdateValue(value: T): T?

    /**
     * This method will be invoked
     * the events will be called after the update value was updated to the cache
     * All events should be called with the [cachedValue] if it is not null
     */
    fun determineEventsToCall(updateValue: T, cachedValue: T?): List<IEvent>

    /**
     * Merges the information of the [updateValue] into the [cachedValue]
     */
    fun mergeUpdateValue(updateValue: T, cachedValue: T)

    /**
     * Sends the [value] to every network component that shall receive the update.
     * This method is only called if [ICacheList.update] was invoked with fromPacket = false or
     * this side is the Manager
     */
    fun sendUpdatesToOtherComponents(value: T, action: PacketIOUpdateCacheObject.Action) {
        val packet = PacketIOUpdateCacheObject(getIdentificationName(), value, action)
        if (CloudAPI.instance.isManager()) {
            val server = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyServer<*>
            server.getClientManager().sendPacketToAllAuthenticatedClients(packet)
        } else {
            val client = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyClient
            client.sendUnitQuery(packet)
        }
    }

    /**
     * Adds the specified [value] to the cache
     */
    fun addNewValue(value: T)

}