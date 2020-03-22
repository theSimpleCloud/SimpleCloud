package eu.thesimplecloud.api.sync.list

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.sync.list.SynchronizedListObjectRemovedEvent
import eu.thesimplecloud.api.event.sync.list.SynchronizedListObjectUpdatedEvent
import eu.thesimplecloud.api.extension.sendPacketToAllAuthenticatedNonWrapperClients
import eu.thesimplecloud.api.network.packets.sync.list.PacketIORemoveSynchronizedListObject
import eu.thesimplecloud.api.network.packets.sync.list.PacketIOUpdateSynchronizedListObject
import eu.thesimplecloud.api.utils.getAllFieldsFromClassAndSubClasses
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.server.INettyServer
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList

abstract class AbstractSynchronizedObjectList<T : ISynchronizedListObject> : ISynchronizedObjectList<T> {

    protected val values = CopyOnWriteArrayList<T>()

    override fun update(value: T, fromPacket: Boolean) {
        val cachedValue = getCachedObjectByUpdateValue(value)
        if (cachedValue == null) {
            this.values.add(value)
        } else {
            if (cachedValue !== value)
                cachedValue::class.java.getAllFieldsFromClassAndSubClasses().forEach { field ->
                    if (!Modifier.isStatic(field.modifiers)) {
                        field.isAccessible = true
                        field.set(cachedValue, field.get(value))
                    }
                }
        }
        if (CloudAPI.instance.isManager() || fromPacket) {
            CloudAPI.instance.getEventManager().call(SynchronizedListObjectUpdatedEvent(getCachedObjectByUpdateValue(value)!!))
        }
        forwardPacketIfNecessary(PacketIOUpdateSynchronizedListObject(getIdentificationName(), value), fromPacket)
    }

    override fun getAllCachedObjects(): Collection<T> = this.values

    override fun remove(value: T, fromPacket: Boolean) {
        val cachedObject = getCachedObjectByUpdateValue(value) ?: return


        if (CloudAPI.instance.isManager() || fromPacket) {
            this.values.remove(cachedObject)
            CloudAPI.instance.getEventManager().call(SynchronizedListObjectRemovedEvent(cachedObject))
        }

        forwardPacketIfNecessary(PacketIORemoveSynchronizedListObject(getIdentificationName(), value), fromPacket)
    }

    private fun forwardPacketIfNecessary(packet: IPacket, fromPacket: Boolean) {
        if (CloudAPI.instance.isManager()) {
            val server = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyServer<*>
            server.getClientManager().sendPacketToAllAuthenticatedNonWrapperClients(packet)
        } else if (!fromPacket) {
            //send update to the manager
            val client = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyClient
            client.sendUnitQuery(packet)
        }
    }

}