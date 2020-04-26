package eu.thesimplecloud.api.sync.list

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.sync.list.SynchronizedListObjectRemovedEvent
import eu.thesimplecloud.api.event.sync.list.SynchronizedListObjectUpdatedEvent
import eu.thesimplecloud.api.extension.sendPacketToAllAuthenticatedNonWrapperClients
import eu.thesimplecloud.api.network.packets.sync.list.PacketIORemoveSynchronizedListObject
import eu.thesimplecloud.api.network.packets.sync.list.PacketIOUpdateSynchronizedListObject
import eu.thesimplecloud.api.sync.`object`.SynchronizedObjectHolder
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.server.INettyServer
import java.util.concurrent.CopyOnWriteArrayList

abstract class AbstractSynchronizedObjectList<T : ISynchronizedListObject> : ISynchronizedObjectList<T> {

    protected val values = CopyOnWriteArrayList<SynchronizedObjectHolder<T>>()

    override fun update(value: T, fromPacket: Boolean) {
        val cachedValueHolder = getCachedObjectByUpdateValue(value)
        if (cachedValueHolder == null) {
            this.values.add(SynchronizedObjectHolder(value))
        } else {
            if (cachedValueHolder.obj !== value) {
                cachedValueHolder.obj = value
            }
        }
        if (CloudAPI.instance.isManager() || fromPacket) {
            CloudAPI.instance.getEventManager().call(SynchronizedListObjectUpdatedEvent(getCachedObjectByUpdateValue(value)!! as SynchronizedObjectHolder<ISynchronizedListObject>))
        }
        forwardPacketIfNecessary(PacketIOUpdateSynchronizedListObject(getIdentificationName(), value), fromPacket)
    }

    override fun getAllCachedObjects(): Collection<SynchronizedObjectHolder<T>> = this.values

    override fun remove(value: T, fromPacket: Boolean) {
        val cachedObject = getCachedObjectByUpdateValue(value) ?: return


        if (CloudAPI.instance.isManager() || fromPacket) {
            this.values.remove(cachedObject)
            CloudAPI.instance.getEventManager().call(SynchronizedListObjectRemovedEvent(cachedObject as SynchronizedObjectHolder<ISynchronizedListObject>))
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