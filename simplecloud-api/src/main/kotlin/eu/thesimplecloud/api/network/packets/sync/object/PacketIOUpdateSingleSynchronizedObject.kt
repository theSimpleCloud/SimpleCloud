package eu.thesimplecloud.api.network.packets.sync.`object`

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.sync.`object`.ISingleSynchronizedObject
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOUpdateSingleSynchronizedObject() : JsonPacket() {

    constructor(synchronizedObject: ISingleSynchronizedObject) : this() {
        this.jsonData.append("class", synchronizedObject::class.java.name).append("synchronizedObject", synchronizedObject)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val className = this.jsonData.getString("class") ?: return contentException("class")
        try {
            val synchronizedObject = this.jsonData.getObject("synchronizedObject", Class.forName(
                    className,
                    true,
                    connection.getCommunicationBootstrap().getClassLoaderToSearchObjectPacketsClasses()
            )) as ISingleSynchronizedObject
            CloudAPI.instance.getSingleSynchronizedObjectManager().updateObject(synchronizedObject, true)
        } catch (ex: ClassNotFoundException) {
            throw ex
        }
        return unit()
    }
}