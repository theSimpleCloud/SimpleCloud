/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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

package eu.thesimplecloud.api.servicegroup.impl.updater

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.cachelist.value.AbstractCacheValueUpdater
import eu.thesimplecloud.api.service.version.ServiceVersion
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroupUpdater
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.jsonlib.JsonLibExclude

open class DefaultServiceGroupUpdater(
    @JsonLibExclude private val serviceGroup: ICloudServiceGroup
) : AbstractCacheValueUpdater(), ICloudServiceGroupUpdater {

    override fun getServiceGroup(): ICloudServiceGroup {
        return this.serviceGroup
    }

    override fun update(): ICommunicationPromise<Unit> {
        return CloudAPI.instance.getCloudServiceGroupManager().update(getServiceGroup())
    }

    override fun getTemplateName(): String {
        return getChangedValue("templateName") ?: serviceGroup.getTemplateName()
    }

    override fun setTemplateName(name: String) {
        changes["templateName"] = name
    }

    override fun getServiceVersion(): ServiceVersion {
        return getChangedValue("serviceVersion") ?: serviceGroup.getServiceVersion()
    }

    override fun setServiceVersion(serviceVersion: ServiceVersion) {
        changes["serviceVersion"] = serviceVersion
    }

    override fun getMinimumMemory(): Int {
        return getChangedValue("minimumMemory") ?: serviceGroup.getMinimumMemory()
    }

    override fun setMinimumMemory(memory: Int) {
        changes["minimumMemory"] = memory
    }

    override fun getMaxMemory(): Int {
        return getChangedValue("maxMemory") ?: serviceGroup.getMaxMemory()
    }

    override fun setMaxMemory(memory: Int) {
        changes["maxMemory"] = memory
    }

    override fun getMaxPlayers(): Int {
        return getChangedValue("maxPlayers") ?: serviceGroup.getMaxPlayers()
    }

    override fun setMaxPlayers(maxPlayers: Int) {
        changes["maxPlayers"] = maxPlayers
    }

    override fun getMinimumOnlineServiceCount(): Int {
        return getChangedValue("minimumOnlineServiceCount") ?: serviceGroup.getMinimumOnlineServiceCount()
    }

    override fun setMinimumOnlineServiceCount(count: Int) {
        changes["minimumOnlineServiceCount"] = count
    }

    override fun getMaximumOnlineServiceCount(): Int {
        return getChangedValue("maximumOnlineServiceCount") ?: serviceGroup.getMaximumOnlineServiceCount()
    }

    override fun setMaximumOnlineServiceCount(count: Int) {
        changes["maximumOnlineServiceCount"] = count
    }

    override fun isInMaintenance(): Boolean {
        return getChangedValue("maintenance") ?: serviceGroup.isInMaintenance()
    }

    override fun setMaintenance(maintenance: Boolean) {
        changes["maintenance"] = maintenance
    }

    override fun isForceCopyTemplates(): Boolean {
        return getChangedValue("forceCopyTemplates") ?: serviceGroup.isForceCopyTemplates()
    }

    override fun setForceCopyTemplates(forceCopyTemplates: Boolean) {
        changes["forceCopyTemplates"] = forceCopyTemplates
    }

    override fun getPercentToStartNewService(): Int {
        return getChangedValue("percentToStartNewService") ?: serviceGroup.getPercentToStartNewService()
    }

    override fun setPercentToStartNewService(percentage: Int) {
        changes["percentToStartNewService"] = percentage
    }

    override fun getWrapperName(): String? {
        return getChangedValue("wrapperName") ?: serviceGroup.getWrapperName()
    }

    override fun setWrapperName(name: String?) {
        changes["wrapperName"] = name
    }

    override fun getPermission(): String? {
        return getChangedValue("permission") ?: serviceGroup.getPermission()
    }

    override fun setPermission(permission: String?) {
        changes["permission"] = permission
    }

    override fun isStateUpdatingEnabled(): Boolean {
        return getChangedValue("stateUpdating") ?: serviceGroup.isStateUpdatingEnabled()
    }

    override fun setStateUpdating(stateUpdating: Boolean) {
        changes["stateUpdating"] = stateUpdating
    }
}