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

package eu.thesimplecloud.api.servicegroup.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.cachelist.AbstractCacheList
import eu.thesimplecloud.api.cachelist.ICacheObjectUpdater
import eu.thesimplecloud.api.event.group.CloudServiceGroupUpdatedEvent
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
            val serviceToUse = cachedValue ?: updateValue
            return listOf(CloudServiceGroupUpdatedEvent(serviceToUse))
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