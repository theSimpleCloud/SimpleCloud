package eu.thesimplecloud.api.sync.list.manager
import eu.thesimplecloud.api.network.packets.sync.list.PacketIOUpdateSynchronizedListObject
import eu.thesimplecloud.api.sync.list.ISynchronizedListObject
import eu.thesimplecloud.api.sync.list.ISynchronizedObjectList
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.combineAllPromises

interface ISynchronizedObjectListManager {

    /**
     * Registers a synchronized object list linked to the objects name
     * @return a promise that completes when the list was synchronized.
     */
    fun registerSynchronizedObjectList(synchronizedObjectList: ISynchronizedObjectList<out ISynchronizedListObject>, syncContent: Boolean = true): ICommunicationPromise<Unit>

    /**
     * Returns the [ISynchronizedObjectList] found by the specified [name]
     */
    fun getSynchronizedObjectList(name: String): ISynchronizedObjectList<ISynchronizedListObject>?

    /**
     * Removes the [ISynchronizedObjectList] registered to the specified [name]
     */
    fun unregisterSynchronizedObjectList(name: String)

    /**
     * Synchronizes the content of a [ISynchronizedObjectList] with the specified [connection]
     */
    fun synchronizeListWithConnection(synchronizedObjectList: ISynchronizedObjectList<out ISynchronizedListObject>, connection: IConnection): ICommunicationPromise<Unit> {
        return synchronizedObjectList.getAllCachedObjects()
                .map { connection.sendUnitQuery(PacketIOUpdateSynchronizedListObject(synchronizedObjectList.getIdentificationName(), it)) }
                .combineAllPromises()
    }

}