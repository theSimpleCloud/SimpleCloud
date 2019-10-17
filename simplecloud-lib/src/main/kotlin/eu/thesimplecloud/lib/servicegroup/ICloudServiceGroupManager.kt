package eu.thesimplecloud.lib.servicegroup

import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.lib.manager.ICacheManager
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceType
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudLobbyGroup
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudProxyGroup
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.lib.servicegroup.impl.DefaultLobbyGroup
import eu.thesimplecloud.lib.servicegroup.impl.DefaultProxyGroup
import eu.thesimplecloud.lib.servicegroup.impl.DefaultServerGroup

interface ICloudServiceGroupManager {

    /**
     * Updates or adds a [ICloudServiceGroup]
     */
    fun updateGroup(cloudServiceGroup: ICloudServiceGroup)


    /**
     * Creates a new [ICloudServerGroup] by the specified parameters and returns a promise that is called when the group is registered
     */
    fun createServerGroup(
            groupName: String,
            templateName: String,
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
    ): ICommunicationPromise<ICloudServerGroup> =
            createServiceGroup(DefaultServerGroup(
                    groupName,
                    templateName,
                    memory,
                    maxPlayers,
                    minimumOnlineServiceCount,
                    maximumOnlineServiceCount,
                    maintenance,
                    static,
                    percentToStartNewService,
                    wrapperName,
                    modulesToCopy,
                    hiddenAtProxyGroups
            )) as ICommunicationPromise<ICloudServerGroup>

    /**
     * Creates a new [ICloudLobbyGroup] by the specified parameters and returns a promise that is called when the group is registered
     */
    fun createLobbyGroup(
            groupName: String,
            templateName: String,
            memory: Int,
            maxPlayers: Int,
            minimumOnlineServiceCount: Int,
            maximumOnlineServiceCount: Int,
            maintenance: Boolean,
            static: Boolean,
            percentToStartNewService: Int,
            wrapperName: String?,
            priority: Int,
            permission: String?,
            modulesToCopy: List<String> = emptyList(),
            hiddenAtProxyGroups: List<String> = emptyList()
    ): ICommunicationPromise<ICloudLobbyGroup> =
            createServiceGroup(DefaultLobbyGroup(
                    groupName,
                    templateName,
                    memory,
                    maxPlayers,
                    minimumOnlineServiceCount,
                    maximumOnlineServiceCount,
                    maintenance,
                    static,
                    percentToStartNewService,
                    wrapperName,
                    priority,
                    permission,
                    modulesToCopy,
                    hiddenAtProxyGroups
            )) as ICommunicationPromise<ICloudLobbyGroup>

    /**
     * Creates a new [ICloudProxyGroup] by the specified parameters and returns a promise that is called when the group is registered
     */
    fun createProxyGroup(
            groupName: String,
            templateName: String,
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
    ): ICommunicationPromise<ICloudProxyGroup> =
            createServiceGroup(DefaultProxyGroup(
                    groupName,
                    templateName,
                    memory,
                    maxPlayers,
                    minimumOnlineServiceCount,
                    maximumOnlineServiceCount,
                    maintenance,
                    static,
                    percentToStartNewService,
                    wrapperName,
                    startPort,
                    modulesToCopy
            )) as ICommunicationPromise<ICloudProxyGroup>

    /**
     * Creates a service group and returns a promise that is called when the group is registered
     */
    fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudServiceGroup>

    /**
     * Returns a list of all registered [ICloudServiceGroup]
     */
    fun getAllGroups(): List<ICloudServiceGroup>

    /**
     * Returns the [ICloudServiceGroup] found by the specified name
     */
    fun getServiceGroup(name: String): ICloudServiceGroup? = getAllGroups().firstOrNull { it.getName().equals(name, true) }

    /**
     * Returns the [ICloudServerGroup] found by the specified name
     */
    fun getServerGroup(name: String): ICloudServerGroup? = getServerGroups().firstOrNull { it.getName().equals(name, true) }

    /**
     * Returns the [ICloudLobbyGroup] found by the specified name
     */
    fun getLobbyGroup(name: String): ICloudLobbyGroup? = getLobbyGroups().firstOrNull { it.getName().equals(name, true) }

    /**
     * Returns the [ICloudProxyGroup] found by the specified name
     */
    fun getProxyGroup(name: String): ICloudProxyGroup? = getProxyGroups().firstOrNull { it.getName().equals(name, true) }

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

    /**
     * Starts a new service by the specified group
     */
    fun startNewService(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudService>

    /**
     * Deletes the specified service group from the cloud.
     * The promise will be called when the operation is completed. [ICommunicationPromise.isSuccess] indicates whether the operation was successful.
     */
    fun deleteServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit>

}