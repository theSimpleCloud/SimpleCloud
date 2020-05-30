package eu.thesimplecloud.api.network.packets.sync.cachelist

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOUpdateCacheObject() : JsonPacket() {

    constructor(cacheListName: String, value: Any, action: Action) : this() {
        this.jsonData.append("cacheListName", cacheListName)
                .append("value", value)
                .append("valueClass", value::class.java.name)
                .append("action", action)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val cacheListName = this.jsonData.getString("cacheListName")
                ?: return contentException("cacheListName")
        val valueClassName = this.jsonData.getString("valueClass")
                ?: return contentException("valueClass")
        val action = this.jsonData.getObject("action", Action::class.java)
                ?: return contentException("action")
        val valueClass = Class.forName(
                valueClassName,
                true,
                connection.getCommunicationBootstrap().getClassLoaderToSearchObjectPacketsClasses()
        )
        val value = this.jsonData.getObject("value", valueClass) ?: return contentException("value")

        when (action) {
            Action.UPDATE -> {
                CloudAPI.instance.getCacheListManager().getCacheListenerByName(cacheListName)
                        ?.update(value, true)
            }
            Action.DELETE -> {
                CloudAPI.instance.getCacheListManager().getCacheListenerByName(cacheListName)
                        ?.delete(value, true)
            }
        }
        return unit()
    }

    enum class Action {
        UPDATE, DELETE
    }
}