package eu.thesimplecloud.api.service.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.cachelist.AbstractCacheList
import eu.thesimplecloud.api.cachelist.ICacheObjectUpdater
import eu.thesimplecloud.api.event.service.*
import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ICloudServiceManager
import eu.thesimplecloud.api.service.ServiceState

abstract class AbstractCloudServiceManager : AbstractCacheList<ICloudService>(), ICloudServiceManager {

    private val updater = object: ICacheObjectUpdater<ICloudService> {

        override fun getCachedObjectByUpdateValue(value: ICloudService): ICloudService? {
            return getCloudServiceByName(value.getName())
        }

        override fun determineEventsToCall(updateValue: ICloudService, cachedValue: ICloudService?): List<IEvent> {
            val serviceToUse = cachedValue ?: updateValue
            if (cachedValue == null){
                return listOf(CloudServiceRegisteredEvent(serviceToUse), CloudServiceUpdatedEvent(serviceToUse))
            }
            val nowStarting = cachedValue.getState() == ServiceState.PREPARED && updateValue.getState() == ServiceState.STARTING
            val nowOnline = !cachedValue.isOnline() && updateValue.isOnline()
            val nowConnected = !cachedValue.isAuthenticated() && updateValue.isAuthenticated()

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
            return events
        }

        override fun mergeUpdateValue(updateValue: ICloudService, cachedValue: ICloudService) {
            cachedValue.setMOTD(updateValue.getMOTD())
            cachedValue.setOnlineCount(updateValue.getOnlineCount())
            cachedValue.setState(updateValue.getState())
            cachedValue.setAuthenticated(updateValue.isAuthenticated())
            cachedValue.setLastUpdate(System.currentTimeMillis())
            cachedValue as DefaultCloudService
            cachedValue.setWrapperName(updateValue.getWrapperName())
            cachedValue.setPort(updateValue.getPort())
            cachedValue.propertyMap = HashMap(updateValue.getProperties() as Map<String, Property<*>>)
        }

        override fun addNewValue(value: ICloudService) {
            value.setLastUpdate(System.currentTimeMillis())
            values.add(value)
        }

        override fun getIdentificationName(): String {
            return "service-cache"
        }

    }

    override fun getUpdater(): ICacheObjectUpdater<ICloudService> {
        return this.updater
    }

    override fun deleteCloudService(name: String) {
        getCloudServiceByName(name)?.let {
            this.delete(it)
        }
    }

    override fun delete(value: ICloudService, fromPacket: Boolean) {
        super<AbstractCacheList>.delete(value, fromPacket)
        CloudAPI.instance.getEventManager().call(CloudServiceUnregisteredEvent(value))
    }
}