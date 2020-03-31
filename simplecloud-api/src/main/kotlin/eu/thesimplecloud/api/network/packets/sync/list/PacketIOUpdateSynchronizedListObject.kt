package eu.thesimplecloud.api.network.packets.sync.list

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.sync.list.ISynchronizedListObject
import eu.thesimplecloud.api.sync.list.ISynchronizedObjectList
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOUpdateSynchronizedListObject() : JsonPacket() {

    constructor(name: String, obj: ISynchronizedListObject) : this() {
        this.jsonData.append("name", name).append("obj", obj).append("className", obj::class.java.name)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val name = this.jsonData.getString("name") ?: return contentException("name")
        val className = this.jsonData.getString("className") ?: return contentException("className")
        try {
            val synchronizedObject = this.jsonData.getObject("obj", Class.forName(className)) as ISynchronizedListObject?
                    ?: return contentException("obj")
            val synchronizedObjectList: ISynchronizedObjectList<ISynchronizedListObject>? = CloudAPI.instance.getSynchronizedObjectListManager().getSynchronizedObjectList(name)
            synchronizedObjectList ?: return failure(NoSuchElementException())
            synchronizedObjectList.update(synchronizedObject, fromPacket = true)

        } catch (ex: Exception) {
            throw ex
        }
        return unit()
    }
}