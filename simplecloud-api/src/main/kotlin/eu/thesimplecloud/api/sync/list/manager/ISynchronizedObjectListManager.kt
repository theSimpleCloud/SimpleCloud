package eu.thesimplecloud.api.sync.list.manager
import eu.thesimplecloud.api.sync.list.ISynchronizedListObject
import eu.thesimplecloud.api.sync.list.ISynchronizedObjectList

interface ISynchronizedObjectListManager {

    /**
     * Registers a synchronized object list linked to the objects name
     */
    fun registerSynchronizedObjectList(synchronizedObjectList: ISynchronizedObjectList<out ISynchronizedListObject>)

    /**
     * Returns the [ISynchronizedObjectList] found by the specified [name]
     */
    fun getSynchronizedObjectList(name: String): ISynchronizedObjectList<ISynchronizedListObject>?

    /**
     * Removes the [ISynchronizedObjectList] registered to the specified [name]
     */
    fun unregisterSynchronizedObjectList(name: String)

}