package eu.thesimplecloud.api.sync.list.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.network.packets.sync.list.PacketIOGetAllCachedSynchronizedListObjects
import eu.thesimplecloud.api.sync.list.ISynchronizedListObject
import eu.thesimplecloud.api.sync.list.ISynchronizedObjectList
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.concurrent.ConcurrentHashMap

class SynchronizedObjectListManager : ISynchronizedObjectListManager {

    private val nameToSynchronizedObjectList = ConcurrentHashMap<String, ISynchronizedObjectList<out ISynchronizedListObject>>()


    override fun registerSynchronizedObjectList(synchronizedObjectList: ISynchronizedObjectList<out ISynchronizedListObject>, syncContent: Boolean): ICommunicationPromise<Unit> {
        if (syncContent && CloudAPI.instance.isManager()) {
            val oldObject = getSynchronizedObjectList(synchronizedObjectList.getIdentificationName())
            oldObject?.let { oldList ->
                oldList.getAllCachedObjects().forEach { oldList.remove(it) }
            }
        }
        this.nameToSynchronizedObjectList[synchronizedObjectList.getIdentificationName()] = synchronizedObjectList
        if (syncContent) {
            if (!CloudAPI.instance.isManager()) {
                val client = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyClient
                return client.sendUnitQueryAsync(PacketIOGetAllCachedSynchronizedListObjects(synchronizedObjectList.getIdentificationName()), 4000)
            } else {
                //manager
                synchronizedObjectList as ISynchronizedObjectList<ISynchronizedListObject>
                synchronizedObjectList.getAllCachedObjects().forEach { synchronizedObjectList.update(it) }
            }
        }
        return CommunicationPromise.of(Unit)
    }

    override fun getSynchronizedObjectList(name: String): ISynchronizedObjectList<ISynchronizedListObject>? = this.nameToSynchronizedObjectList[name] as ISynchronizedObjectList<ISynchronizedListObject>?

    override fun unregisterSynchronizedObjectList(name: String) {
        this.nameToSynchronizedObjectList.remove(name)
    }

}