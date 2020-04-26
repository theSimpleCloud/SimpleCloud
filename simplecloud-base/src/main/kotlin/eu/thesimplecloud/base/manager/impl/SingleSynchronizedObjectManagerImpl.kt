package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.api.network.packets.sync.`object`.PacketIOUpdateSingleSynchronizedObject
import eu.thesimplecloud.api.sync.`object`.AbstractSingleSynchronizedObjectManager
import eu.thesimplecloud.api.sync.`object`.ISingleSynchronizedObject
import eu.thesimplecloud.api.sync.`object`.SynchronizedObjectHolder
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class SingleSynchronizedObjectManagerImpl : AbstractSingleSynchronizedObjectManager() {

    private val clientsToUpdate = HashMap<String, MutableSet<IConnection>>()

    fun addClientToUpdateObject(synchronizedObject: ISingleSynchronizedObject, connection: IConnection) {
        val list = clientsToUpdate.getOrPut(synchronizedObject.getName()) { HashSet() }
        list.add(connection)
    }

    fun unregisterClient(connection: IConnection) {
        clientsToUpdate.values.forEach { it.remove(connection) }
    }

    override fun <T : ISingleSynchronizedObject> updateObject(synchronizedObject: T, fromPacket: Boolean) : SynchronizedObjectHolder<T> {
        val objectHolder = super.updateObject(synchronizedObject, fromPacket)
        clientsToUpdate[synchronizedObject.getName()]?.forEach { it.sendUnitQuery(PacketIOUpdateSingleSynchronizedObject(synchronizedObject)) }
        return objectHolder
    }

    override fun <T : ISingleSynchronizedObject> requestSingleSynchronizedObject(name: String, clazz: Class<T>): ICommunicationPromise<SynchronizedObjectHolder<T>> {
        return CommunicationPromise.ofNullable(getObject(name), NoSuchElementException())
    }
}