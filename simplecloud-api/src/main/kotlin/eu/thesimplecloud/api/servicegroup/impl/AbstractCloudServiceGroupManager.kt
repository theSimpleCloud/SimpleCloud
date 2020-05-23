package eu.thesimplecloud.api.servicegroup.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.cachelist.AbstractCacheList
import eu.thesimplecloud.api.cachelist.ICacheObjectUpdater
import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroupManager

abstract class AbstractCloudServiceGroupManager : AbstractCacheList<ICloudServiceGroup>(), ICloudServiceGroupManager {

    private val updater = object : ICacheObjectUpdater<ICloudServiceGroup> {
        override fun getIdentificationName(): String {
            return "group-cache"
        }

        override fun getCachedObjectByUpdateValue(value: ICloudServiceGroup): ICloudServiceGroup? {
            return getServiceGroupByName(value.getName())
        }

        override fun determineEventsToCall(updateValue: ICloudServiceGroup, cachedValue: ICloudServiceGroup?): List<IEvent> {
            return emptyList()
        }

        override fun mergeUpdateValue(updateValue: ICloudServiceGroup, cachedValue: ICloudServiceGroup) {
            cachedValue.setMaintenance(updateValue.isInMaintenance())
            cachedValue.setMaxMemory(updateValue.getMaxMemory())
            cachedValue.setMaxPlayers(updateValue.getMaxPlayers())
            cachedValue.setMaximumOnlineServiceCount(updateValue.getMaximumOnlineServiceCount())
            cachedValue.setMinimumOnlineServiceCount(updateValue.getMinimumOnlineServiceCount())
            cachedValue.setPercentToStartNewService(updateValue.getPercentToStartNewService())
            cachedValue.setTemplateName(updateValue.getTemplateName())
        }

        override fun addNewValue(value: ICloudServiceGroup) {
            values.add(value)
        }

    }

    override fun delete(value: ICloudServiceGroup, fromPacket: Boolean) {
        if (CloudAPI.instance.getCloudServiceManager().getCloudServicesByGroupName(value.getName()).isNotEmpty())
            throw IllegalStateException("Cannot delete service group while services of this group are registered.")
        super<AbstractCacheList>.delete(value, fromPacket)
    }

    override fun getUpdater(): ICacheObjectUpdater<ICloudServiceGroup> {
        return this.updater
    }

}