package eu.thesimplecloud.client.impl

import eu.thesimplecloud.api.network.packets.sync.`object`.PacketIOUpdateSynchronizedObject
import eu.thesimplecloud.api.sync.`object`.AbstractSynchronizedObjectManager
import eu.thesimplecloud.api.sync.`object`.ISynchronizedObject
import eu.thesimplecloud.client.packets.PacketOutGetSynchronizedObject
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.flatten

class SynchronizedObjectManagerImpl(private val client: INettyClient) : AbstractSynchronizedObjectManager() {


    override fun updateObject(synchronizedObject: ISynchronizedObject, fromPacket: Boolean) {
        if (fromPacket) {
            super.updateObject(synchronizedObject, fromPacket)
        } else {
            client.sendUnitQuery(PacketIOUpdateSynchronizedObject(synchronizedObject))
        }
    }

    override fun <T : ISynchronizedObject> requestSynchronizedObject(name: String, clazz: Class<T>): ICommunicationPromise<T> {
        return client.getPacketIdsSyncPromise().then { client.sendQuery(PacketOutGetSynchronizedObject(name), clazz).addResultListener { updateObject(it, true) } }.flatten()
    }
}