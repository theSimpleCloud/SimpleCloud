package eu.thesimplecloud.base.manager.network.packets

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.sync.`object`.ISingleSynchronizedObject
import eu.thesimplecloud.base.manager.impl.SingleSynchronizedObjectManagerImpl
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketInGetSynchronizedObject : ObjectPacket<String>() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val synchronizedObjectManager = CloudAPI.instance.getSingleSynchronizedObjectManager() as SingleSynchronizedObjectManagerImpl
        val value = this.value ?: return contentException("value")
        val synchronizedObject = synchronizedObjectManager.getObject<ISingleSynchronizedObject>(value)
        synchronizedObject?.let {
            synchronizedObjectManager.addClientToUpdateObject(it.obj, connection)
        }
        return CommunicationPromise.ofNullable(synchronizedObject?.obj, NoSuchElementException())
    }
}