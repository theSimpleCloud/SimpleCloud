package eu.thesimplecloud.client.impl

import eu.thesimplecloud.api.network.packets.syncobject.PacketIOUpdateSynchronizedObject
import eu.thesimplecloud.api.syncobject.AbstractSynchronizedObjectManager
import eu.thesimplecloud.api.syncobject.ISynchronizedObject
import eu.thesimplecloud.api.syncobject.ISynchronizedObjectManager
import eu.thesimplecloud.client.packets.PacketOutGetSynchronizedObject
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class SynchronizedObjectManagerImpl(private val client: INettyClient) : AbstractSynchronizedObjectManager() {


    override fun updateObject(synchronizedObject: ISynchronizedObject, fromPacket: Boolean) {
        if (fromPacket) {
            super.updateObject(synchronizedObject, fromPacket)
        } else {
            client.sendUnitQuery(PacketIOUpdateSynchronizedObject(synchronizedObject))
        }
    }

    override fun <T : ISynchronizedObject> requestSynchronizedObject(name: String, clazz: Class<T>): ICommunicationPromise<T> {
        return client.sendQuery(PacketOutGetSynchronizedObject(name), clazz).addResultListener { updateObject(it, true) }
    }
}