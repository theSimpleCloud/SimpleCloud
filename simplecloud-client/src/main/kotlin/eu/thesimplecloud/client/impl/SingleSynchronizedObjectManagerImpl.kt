package eu.thesimplecloud.client.impl

import eu.thesimplecloud.api.network.packets.sync.`object`.PacketIOUpdateSingleSynchronizedObject
import eu.thesimplecloud.api.sync.`object`.AbstractSingleSynchronizedObjectManager
import eu.thesimplecloud.api.sync.`object`.ISingleSynchronizedObject
import eu.thesimplecloud.api.sync.`object`.SynchronizedObjectHolder
import eu.thesimplecloud.client.packets.PacketOutGetSynchronizedObject
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class SingleSynchronizedObjectManagerImpl(private val client: INettyClient) : AbstractSingleSynchronizedObjectManager() {


    override fun <T : ISingleSynchronizedObject> updateObject(synchronizedObject: T, fromPacket: Boolean): SynchronizedObjectHolder<T> {
        if (fromPacket) {
            return super.updateObject(synchronizedObject, fromPacket)
        } else {
            client.sendUnitQuery(PacketIOUpdateSingleSynchronizedObject(synchronizedObject))

            val cachedHolder = getObject<T>(synchronizedObject.getName())
            if (cachedHolder == null) {
                val newHolder: SynchronizedObjectHolder<T> = SynchronizedObjectHolder(synchronizedObject)
                this.nameToValue[synchronizedObject.getName()] = newHolder as SynchronizedObjectHolder<ISingleSynchronizedObject>
                return newHolder
            }
            return cachedHolder
        }
    }

    override fun <T : ISingleSynchronizedObject> requestSingleSynchronizedObject(name: String, clazz: Class<T>): ICommunicationPromise<SynchronizedObjectHolder<T>> {
        val objectPromise = client.sendQueryAsync(PacketOutGetSynchronizedObject(name), clazz, 2000)
        return objectPromise.then { updateObject(it, true) }
    }
}