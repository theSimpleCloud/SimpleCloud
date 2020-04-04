package eu.thesimplecloud.api.network.packets.sync.list

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.combineAllPromises

class PacketIOGetAllCachedSynchronizedListObjects() : ObjectPacket<String>() {

    constructor(name: String) : this() {
        this.value = name
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val value = this.value ?: return contentException("value")
        val synchronizedObjectList = CloudAPI.instance.getSynchronizedObjectListManager().getSynchronizedObjectList(value)
        synchronizedObjectList ?: return failure(NoSuchElementException("No list object found by the specified name: $value"))
        val allPromises = synchronizedObjectList.getAllCachedObjects().map { connection.sendUnitQuery(PacketIOUpdateSynchronizedListObject(value, it)) }
        return allPromises.combineAllPromises()
    }
}