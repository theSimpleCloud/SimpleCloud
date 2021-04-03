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

import com.google.common.collect.Maps
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ICloudServiceUpdater
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.service.version.ServiceVersion
import eu.thesimplecloud.api.utils.time.Timestamp
import eu.thesimplecloud.clientserverapi.lib.json.PacketExclude
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.jsonlib.JsonLibExclude
import java.util.*
import java.util.concurrent.ConcurrentMap

data class DefaultCloudService(
        private val groupName: String,
        private val serviceNumber: Int,
        private val uniqueId: UUID,
        private val templateName: String,
        @Volatile private var wrapperName: String?,
        @Volatile private var port: Int,
        private val maxMemory: Int,
        @Volatile private var maxPlayers: Int,
        @Volatile private var motd: String,
        private val serviceVersion: ServiceVersion
) : ICloudService {

    @JsonLibExclude
    @PacketExclude
    @Volatile
    private var serviceUpdater: DefaultCloudServiceUpdater? = DefaultCloudServiceUpdater(this)

    @Volatile
    private var serviceState = ServiceState.PREPARED

    @Volatile
    private var onlineCount = 0

    @Volatile
    private var usedMemory = 0

    @Volatile
    private var authenticated = false

    @JsonLibExclude
    @Volatile
    private var lastPlayerUpdate = Timestamp()

    @Volatile
    var propertyMap: ConcurrentMap<String, Property<*>> = Maps.newConcurrentMap()

    override fun getGroupName(): String = this.groupName

    override fun getServiceNumber(): Int = this.serviceNumber

    override fun getUniqueId(): UUID = this.uniqueId

    override fun getServiceVersion(): ServiceVersion = this.serviceVersion

    override fun getTemplateName(): String = this.templateName

    override fun getPort(): Int = this.port

    override fun getUpdater(): ICloudServiceUpdater {
        if (this.serviceUpdater == null) {
            this.serviceUpdater = DefaultCloudServiceUpdater(this)
        }
        return this.serviceUpdater!!
    }

    fun setPort(port: Int) {
        this.port = port
    }

    override fun getWrapperName(): String? = this.wrapperName

    fun setWrapperName(wrapperName: String?) {
        this.wrapperName = wrapperName
    }

    override fun getState(): ServiceState = this.serviceState

    override fun setState(serviceState: ServiceState) {
        getUpdater().setState(serviceState)
    }

    override fun getOnlineCount(): Int {
        return this.onlineCount
    }

    override fun setOnlineCount(amount: Int) {
        getUpdater().setOnlineCount(amount)
    }

    override fun getMaxPlayers(): Int {
        return this.maxPlayers
    }

    override fun setMaxPlayers(amount: Int) {
        getUpdater().setMaxPlayers(amount)
    }

    override fun getMOTD(): String = this.motd

    override fun setMOTD(motd: String) {
        getUpdater().setMOTD(motd)
    }

    override fun isAuthenticated(): Boolean = this.authenticated

    override fun setAuthenticated(authenticated: Boolean) {
        this.authenticated = authenticated
    }

    override fun getMaxMemory(): Int = this.maxMemory

    override fun getUsedMemory(): Int = this.usedMemory

    override fun getLastPlayerUpdate(): Timestamp = this.lastPlayerUpdate

    override fun setLastPlayerUpdate(timeStamp: Timestamp) {
        getUpdater().setLastPlayerUpdate(timeStamp)
    }


    override fun toString(): String {
        return JsonLib.fromObject(this).getAsJsonString()
    }

    override fun getProperties(): Map<String, IProperty<*>> = this.propertyMap

    override fun <T : Any> setProperty(name: String, value: T): IProperty<T> {
        require(value !is Property<*>) { "Cannot set ${value::class.java.name} as property" }
        val property = Property(value)
        this.propertyMap[name] = property
        return property
    }

    override fun clearProperties() {
        this.propertyMap.clear()
    }

    override fun removeProperty(name: String) {
        this.propertyMap.remove(name)
    }

    override fun applyValuesFromUpdater(updater: ICloudServiceUpdater) {
        this.serviceState = updater.getState()
        this.onlineCount = updater.getOnlineCount()
        this.lastPlayerUpdate = updater.getLastPlayerUpdate()
        this.motd = updater.getMOTD()

        val updateService = updater.getCloudService()
        this.authenticated = updateService.isAuthenticated()
        this.maxPlayers = updateService.getMaxPlayers()
        this.wrapperName = updateService.getWrapperName()
        this.port = updateService.getPort()
        this.usedMemory = updateService.getUsedMemory()
        this.propertyMap = this.getMapWithNewestProperties(updateService.getProperties()) as ConcurrentMap<String, Property<*>>

        if (this.getOnlineCount() != updateService.getOnlineCount())
            this.lastPlayerUpdate = Timestamp()
    }

    fun setUsedMemory(usedMemory: Int) {
        this.usedMemory = usedMemory
    }

}