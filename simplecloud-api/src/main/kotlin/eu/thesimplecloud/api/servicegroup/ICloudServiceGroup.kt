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
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.api.service.start.configuration.IServiceStartConfiguration
import eu.thesimplecloud.api.service.start.configuration.ServiceStartConfiguration
import eu.thesimplecloud.api.service.start.future.IServiceStartPromise
import eu.thesimplecloud.api.service.version.ServiceVersion
import eu.thesimplecloud.api.template.ITemplate

interface ICloudServiceGroup {

    /**
     * Returns the name of this service group
     * e.g. Lobby
     */
    fun getName(): String

    /**
     * Returns the template that this service group uses
     * e.g. Lobby
     */
    fun getTemplateName(): String

    /**
     * Returns the template that this service uses
     * e.g. Lobby
     */
    fun getTemplate(): ITemplate = CloudAPI.instance.getTemplateManager().getTemplateByName(getTemplateName()) ?: throw IllegalStateException("Can't find the template of an registered group (group: ${getName()} templates: ${CloudAPI.instance.getTemplateManager().getAllCachedObjects().joinToString { it.getName() }})")

    /**
     * Sets the name of the template for this service group
     */
    fun setTemplateName(name: String)

    /**
     * Returns the type of this service group
     */
    fun getServiceType(): ServiceType

    /**
     * Returns the version, services of this group are running on
     */
    fun getServiceVersion(): ServiceVersion

    /**
     * Sets the [ServiceVersion] of this group
     */
    fun setServiceVersion(serviceVersion: ServiceVersion)

    /**
     * Returns the maximum amount of RAM for the services of this service group in MB
     */
    fun getMaxMemory(): Int

    /**
     * Sets maximum amount of RAM for the services of this service group in MB
     */
    fun setMaxMemory(memory: Int)

    /**
     * Returns the maximum amount of players for the services of this service group
     */
    fun getMaxPlayers(): Int

    /**
     * Sets the maximum amount of players for the services of this service group
     */
    fun setMaxPlayers(maxPlayers: Int)

    /**
     * Returns the minimum amount of services that should be simultaneously in LOBBY state
     */
    fun getMinimumOnlineServiceCount(): Int

    /**
     * Sets the minimum amount of services that should be simultaneously in LOBBY state
     */
    fun setMinimumOnlineServiceCount(count: Int)

    /**
     * Returns the maximum amount of services that should be simultaneously in LOBBY state
     */
    fun getMaximumOnlineServiceCount(): Int

    /**
     * Sets the maximum amount of services that should be simultaneously in LOBBY state
     */
    fun setMaximumOnlineServiceCount(count: Int)

    /**
     * Returns whether this service group is in maintenance
     */
    fun isInMaintenance(): Boolean

    /**
     * Sets the maintenance state of this service group
     */
    fun setMaintenance(maintenance: Boolean)

    /**
     * Returns whether this service is static.
     */
    fun isStatic(): Boolean

    /**
     * Returns the percent of online players that a service must reach until a new service starts.
     */
    fun getPercentToStartNewService(): Int

    /**
     * Sets the percent of online players that a service must reach until a new service starts.
     */
    fun setPercentToStartNewService(percentage: Int)

    /**
     * Returns the wrapper where all service of this group should run and null if there is no specified wrapper.
     */
    fun getWrapperName(): String?

    /**
     * Returns the start priority services of this group will have. Services with higher priority will start first.
     */
    fun getStartPriority(): Int

    /**
     * Returns the permission a player needs to join a service of this group or null if there is no permission set.
     */
    fun getPermission(): String?

    /**
     * Sets the permission a player needs to join a service of this group
     */
    fun setPermission(permission: String?)

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
     * Returns whether the state shall automatically be updated to [ServiceState.VISIBLE]
     */
    fun isStateUpdatingEnabled(): Boolean

    /**
     * Sets whether this group shall update its state automatically to [ServiceState.VISIBLE]
     */
    fun setStateUpdating(stateUpdating: Boolean)

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
    fun update() = CloudAPI.instance.getCloudServiceGroupManager().update(this)

}