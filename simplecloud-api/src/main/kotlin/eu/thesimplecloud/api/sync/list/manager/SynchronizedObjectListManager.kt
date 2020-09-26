/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.api.sync.list.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.network.packets.sync.list.PacketIOGetAllCachedListProperties
import eu.thesimplecloud.api.sync.list.ISynchronizedObjectList
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.concurrent.ConcurrentHashMap

class SynchronizedObjectListManager : ISynchronizedObjectListManager {

    private val nameToSynchronizedObjectList = ConcurrentHashMap<String, ISynchronizedObjectList<out Any>>()


    override fun registerSynchronizedObjectList(synchronizedObjectList: ISynchronizedObjectList<out Any>, syncContent: Boolean): ICommunicationPromise<Unit> {
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
                return client.getConnection()
                        .sendUnitQueryAsync(PacketIOGetAllCachedListProperties(synchronizedObjectList.getIdentificationName()), 4000)
            } else {
                //manager
                synchronizedObjectList as ISynchronizedObjectList<Any>
                synchronizedObjectList.getAllCachedObjects().forEach { synchronizedObjectList.update(it) }
            }
        }
        return CommunicationPromise.of(Unit)
    }

    override fun getSynchronizedObjectList(name: String): ISynchronizedObjectList<Any>? = this.nameToSynchronizedObjectList[name] as ISynchronizedObjectList<Any>?

    override fun unregisterSynchronizedObjectList(name: String) {
        this.nameToSynchronizedObjectList.remove(name)
    }

}