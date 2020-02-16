package eu.thesimplecloud.base.manager.network.packets

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.syncobject.ISynchronizedObject
import eu.thesimplecloud.base.manager.impl.SynchronizedObjectManagerImpl
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketInGetSynchronizedObject : ObjectPacket<String>() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val synchronizedObjectManager = CloudAPI.instance.getSynchronizedObjectManager() as SynchronizedObjectManagerImpl
        val value = this.value ?: return contentException("value")
        val synchronizedObject = synchronizedObjectManager.getObject<ISynchronizedObject>(value)
        synchronizedObject?.let {
            synchronizedObjectManager.addClientToUpdateObject(it, connection)
        }
        return CommunicationPromise.ofNullable(synchronizedObject, NoSuchElementException())
    }
}