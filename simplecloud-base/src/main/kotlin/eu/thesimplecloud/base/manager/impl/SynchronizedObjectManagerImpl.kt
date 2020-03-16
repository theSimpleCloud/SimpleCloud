package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.api.network.packets.sync.`object`.PacketIOUpdateSynchronizedObject
import eu.thesimplecloud.api.sync.`object`.AbstractSynchronizedObjectManager
import eu.thesimplecloud.api.sync.`object`.ISynchronizedObject
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class SynchronizedObjectManagerImpl : AbstractSynchronizedObjectManager() {

    private val clientsToUpdate = HashMap<String, MutableSet<IConnection>>()

    fun addClientToUpdateObject(synchronizedObject: ISynchronizedObject, connection: IConnection) {
        val list = clientsToUpdate.getOrPut(synchronizedObject.getName()) { HashSet() }
        list.add(connection)
    }

    fun unregisterClient(connection: IConnection) {
        clientsToUpdate.values.forEach { it.remove(connection) }
    }

    override fun updateObject(synchronizedObject: ISynchronizedObject, fromPacket: Boolean) {
        super.updateObject(synchronizedObject, fromPacket)

        clientsToUpdate[synchronizedObject.getName()]?.forEach { it.sendUnitQuery(PacketIOUpdateSynchronizedObject(synchronizedObject)) }
    }

    override fun <T : ISynchronizedObject> requestSynchronizedObject(name: String, clazz: Class<T>): ICommunicationPromise<T> {
        return CommunicationPromise.of(getObject<T>(name) as T)
    }
}