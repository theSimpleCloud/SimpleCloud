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

package eu.thesimplecloud.api.servicegroup

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.cachelist.value.ICacheValue
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.api.service.start.configuration.IServiceStartConfiguration
import eu.thesimplecloud.api.service.start.configuration.ServiceStartConfiguration
import eu.thesimplecloud.api.service.start.future.IServiceStartPromise
import eu.thesimplecloud.api.template.ITemplate

interface ICloudServiceGroup : ICacheValue<ICloudServiceGroupUpdater>, ICloudServiceGroupVariables {

    /**
     * Returns the name of this service group
     * e.g. Lobby
     */
    fun getName(): String

    /**
     * Returns the type of this service group
     */
    fun getServiceType(): ServiceType

    /**
     * Returns the template that this service uses
     * e.g. Lobby
     */
    fun getTemplate(): ITemplate = CloudAPI.instance.getTemplateManager().getTemplateByName(getTemplateName())
            ?: throw IllegalStateException("Can't find the template of an registered group (group: ${getName()} templates: ${CloudAPI.instance.getTemplateManager().getAllCachedObjects().joinToString { it.getName() }})")

    /**
     * Returns whether this service is static.
     */
    fun isStatic(): Boolean

    /**
     * Returns the start priority services of this group will have. Services with higher priority will start first.
     */
    fun getStartPriority(): Int

    /**
     * Starts a new service by this group
     * @return a promise which is called when the new service was registered.
     * The promise will fail with:
     * - [NoSuchElementException] if the group does not exist.
     */
    fun startNewService(): IServiceStartPromise = createStartConfiguration().startService()

    /**
     * Returns a new [IServiceStartConfiguration]
     */
    fun createStartConfiguration(): IServiceStartConfiguration = ServiceStartConfiguration(this)

    /**
     * Returns a list of all registered services by this group
     */
    fun getAllServices(): List<ICloudService> = CloudAPI.instance.getCloudServiceManager().getCloudServicesByGroupName(getName())

    /**
     * Returns the amount of online players in this group
     */
    fun getOnlinePlayerCount(): Int = getAllServices().sumBy { it.getOnlineCount() }

    /**
     * Returns the amount of registered services
     */
    fun getRegisteredServiceCount(): Int = getAllServices().size

    /**
     * Returns the amount of online services
     */
    fun getOnlineServiceCount(): Int = getAllServices().filter { it.isOnline() }.size

    /**
     * Stops all services of this group.
     */
    fun shutdownAllServices() = getAllServices().forEach { it.shutdown() }

    /**
     * Deletes this service group from the cloud
     * @throws IllegalStateException if services of this group are registered
     */
    @Throws(IllegalStateException::class)
    fun delete() = CloudAPI.instance.getCloudServiceGroupManager().delete(this)

    /**
     * Updates the group to the network
     */
    fun update() = getUpdater().update()

}