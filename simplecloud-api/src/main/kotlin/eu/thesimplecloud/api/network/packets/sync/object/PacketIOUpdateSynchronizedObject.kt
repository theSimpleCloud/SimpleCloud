package eu.thesimplecloud.api.network.packets.sync.`object`

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.sync.`object`.ISynchronizedObject
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOUpdateSynchronizedObject() : JsonPacket() {

    constructor(synchronizedObject: ISynchronizedObject) : this() {
        this.jsonData.append("class", synchronizedObject::class.java.name).append("synchronizedObject", synchronizedObject)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val className = this.jsonData.getString("class") ?: return contentException("class")
        try {
            val synchronizedObject = this.jsonData.getObject("synchronizedObject", Class.forName(className)) as ISynchronizedObject
            CloudAPI.instance.getSynchronizedObjectManager().updateObject(synchronizedObject, true)
        } catch (ex: ClassNotFoundException) {
            throw ex
        }
        return unit()
    }
}