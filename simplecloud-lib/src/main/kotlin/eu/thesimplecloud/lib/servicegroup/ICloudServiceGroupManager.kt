package eu.thesimplecloud.lib.servicegroup

import eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise.IConnectionPromise
import eu.thesimplecloud.lib.service.ServiceType

interface ICloudServiceGroupManager {

    /**
     * Updates or adds a [ICloudServiceGroup]
     */
    fun updateGroup(cloudServiceGroup: ICloudServiceGroup)

    /**
     * Removes a [ICloudServiceGroup]
     */
    fun removeGroup(cloudServiceGroup: ICloudServiceGroup)

    /**
     * Removes the [ICloudServiceGroup] found by the specified name
     */
    fun removeGroup(name: String)


    /**
     * Creates a new [ICloudServiceGroup] by the specified parameters
     */
    fun createNewGroup(
            groupName: String,
            templateName: String,
            serviceType: ServiceType,
            memory: Int,
            maxPlayers: Int,
            minimumOnlineServiceCount: Int,
            maximumOnlineServiceCount: Int,
            maintenance: Boolean,
            static: Boolean,
            percentToStartNewService: Int,
            wrapperName: String,
            modulesToCopy: List<String> = emptyList()
    ): IConnectionPromise<ICloudServiceGroup>

    /**
     * Returns the [ICloudServiceGroup] found by the specified name
     */
    fun getGroup(name: String): ICloudServiceGroup?

    /**
     * Returns a list of all registered [ICloudServiceGroup]
     */
    fun getAllGroups(): List<ICloudServiceGroup>

    /**
     * Clears the cache of all [ICloudServiceGroup]s
     */
    fun clearCache()

}