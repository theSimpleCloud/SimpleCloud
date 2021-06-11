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

package eu.thesimplecloud.api.service.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.cachelist.AbstractCacheList
import eu.thesimplecloud.api.cachelist.ICacheObjectUpdateExecutor
import eu.thesimplecloud.api.event.service.*
import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ICloudServiceManager
import eu.thesimplecloud.api.service.ICloudServiceUpdater
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.utils.time.Timestamp
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

abstract class AbstractCloudServiceManager : AbstractCacheList<ICloudServiceUpdater, ICloudService>(), ICloudServiceManager {

    private val updater = object: ICacheObjectUpdateExecutor<ICloudServiceUpdater, ICloudService> {

        override fun getCachedObjectByUpdateValue(value: ICloudService): ICloudService? {
            return getCloudServiceByName(value.getName())
        }

        override fun determineEventsToCall(updater: ICloudServiceUpdater, cachedValue: ICloudService?): List<IEvent> {
            val serviceToUse = cachedValue ?: updater.getCloudService()
            if (cachedValue == null){
                return listOf(CloudServiceRegisteredEvent(serviceToUse), CloudServiceUpdatedEvent(serviceToUse))
            }
            val nowStarting = cachedValue.getState() == ServiceState.PREPARED && updater.getState() == ServiceState.STARTING
            val nowOnline = !cachedValue.isOnline() && updater.isServiceJoinable()
            val nowConnected = !cachedValue.isAuthenticated() && updater.isAuthenticated()
            val nowInvisible = cachedValue.getState() == ServiceState.VISIBLE && updater.getState() == ServiceState.INVISIBLE

            val events = ArrayList<IEvent>()
            events.add(CloudServiceUpdatedEvent(cachedValue))

            if (nowStarting) {
                events.add(CloudServiceStartingEvent(cachedValue))
            }
            if (nowConnected) {
                events.add(CloudServiceConnectedEvent(cachedValue))
            }
            if (nowOnline) {
                events.add(CloudServiceStartedEvent(cachedValue))
            }
            if (nowInvisible) {
                events.add(CloudServiceInvisibleEvent(cachedValue))
            }
            return events
        }

        override fun addNewValue(value: ICloudService) {
            value.setLastPlayerUpdate(Timestamp())
            values.add(value)
        }

        override fun getIdentificationName(): String {
            return "service-cache"
        }

    }

    override fun getUpdateExecutor(): ICacheObjectUpdateExecutor<ICloudServiceUpdater, ICloudService> {
        return this.updater
    }

    override fun deleteCloudService(name: String) {
        getCloudServiceByName(name)?.let {
            this.delete(it)
        }
    }

    override fun delete(value: ICloudService, fromPacket: Boolean): ICommunicationPromise<Unit> {
        CloudAPI.instance.getEventManager().call(CloudServiceUnregisteredEvent(value))
        return super<AbstractCacheList>.delete(value, fromPacket)
    }
}