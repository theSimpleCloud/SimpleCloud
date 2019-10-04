package eu.thesimplecloud.lib.servicegroup

import eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise.IConnectionPromise
import eu.thesimplecloud.lib.bootstrap.ICloudBootstrapGetter
import eu.thesimplecloud.lib.service.ServiceType
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudLobbyGroup
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudProxyGroup
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudServerGroup

interface ICloudServiceGroupManager : ICloudBootstrapGetter {

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
     * Creates a new [ICloudServerGroup] by the specified parameters
     */
    fun createNewServerGroup(
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
            wrapperName: String?,
            modulesToCopy: List<String> = emptyList(),
            hiddenAtProxyGroups: List<String> = emptyList()
    ): IConnectionPromise<ICloudServerGroup>

    /**
     * Creates a new [ICloudLobbyGroup] by the specified parameters
     */
    fun createNewLobbyGroup(
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
            wrapperName: String?,
            priority: Int,
            permission: String,
            modulesToCopy: List<String> = emptyList(),
            hiddenAtProxyGroups: List<String> = emptyList()
    ): IConnectionPromise<ICloudLobbyGroup>

    /**
     * Creates a new [ICloudProxyGroup] by the specified parameters
     */
    fun createNewProxyGroup(
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
            startPort: Int,
            modulesToCopy: List<String> = emptyList()
    ): IConnectionPromise<ICloudProxyGroup>

    /**
     * Returns a list of all registered [ICloudServiceGroup]
     */
    fun getAllGroups(): List<ICloudServiceGroup>

    /**
     * Returns the [ICloudServiceGroup] found by the specified name
     */
    fun getGroup(name: String): ICloudServiceGroup? = getAllGroups().firstOrNull { it.getName().equals(name, true) }

    /**
     * Returns all registered proxy groups
     */
    fun getProxyGroups(): List<ICloudProxyGroup> = getAllGroups().filter { it.getServiceType() == ServiceType.PROXY }.map { it as ICloudProxyGroup }

    /**
     * Returns all registered lobby groups
     */
    fun getLobbyGroups(): List<ICloudLobbyGroup> = getAllGroups().filter { it.getServiceType() == ServiceType.LOBBY }.map { it as ICloudLobbyGroup }

    /**
     * Returns all registered server groups
     */
    fun getServerGroups(): List<ICloudServerGroup> = getAllGroups().filter { it.getServiceType() == ServiceType.SERVER }.map { it as ICloudServerGroup }

    /**
     * Clears the cache of all [ICloudServiceGroup]s
     */
    fun clearCache()

}