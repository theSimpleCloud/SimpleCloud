package eu.thesimplecloud.api.servicegroup
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.api.service.ServiceVersion

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
     * Starts a new service by this group
     * @return a promise which is called when the new service was registered.
     * The promise will fail with:
     * - [NoSuchElementException] if the group does not exist.
     */
    fun startNewService(): ICommunicationPromise<ICloudService> = CloudAPI.instance.getCloudServiceGroupManager().startNewService(this)

    /**
     * Returns a list of all registered services by this group
     */
    fun getAllRunningServices(): List<ICloudService> = CloudAPI.instance.getCloudServiceManger().getCloudServicesByGroupName(getName())

    /**
     * Stops all services by this group.
     */
    fun stopAllRunningServices() = getAllRunningServices().forEach { it.shutdown() }

    /**
     * Deletes this service group from the cloud
     * [ICommunicationPromise.isSuccess] will indicate whether the deletion was successful
     */
    fun delete(): ICommunicationPromise<Unit> = CloudAPI.instance.getCloudServiceGroupManager().deleteServiceGroup(this)

}